# spring-security-session

## Session을 이용한 Spring Security
Jw

### 1. AccountContext
- SpringSecurity에서 유저 정보를 담는 객체
- DB에서 유저의 정보를 Account라고 저장하듯이 시큐리티도 마찬가지로 객체가 필요함
- `UserDtail`인터페이스의 구현체를 대상으로 인식함
- `UserDetail`의 서브 인터페이스인 `User` 인터페이스를 구현해 커스텀 객체를 만듬
- `UserDetailsService`가 DB에서 유저정보(사용자가 입력한 email,id)를 이용해 값을 찾은 후, `AccountContext`를 객체로 반환한다.
    ```
  @Getter
    @Setter
    public class AccountContext extends User {
        private Account account;
        public AccountContext(Account account, Collection<? extends GrantedAuthority> authorities) {
            super(account.getEmail(), account.getPassword(), authorities);
            this.account = account;
        }
    }
    ```
  
  
### 2. UserDetailsService
- `AuthenticationProvider` 구현체가 인증작업을 할 때, 사용하는 객체
- DB에 직접적으로 접근하여, 해당유저가 존재하는지 확인한 후, 존재한다면 `UserDetail` 타입으로 반환한다.
- DB에 접근하기 위하여, `AccountRepository`를 빈으로 주입받는다.
- User의 권한정보는 `GrantedAuthority`의 컬렉션 타입으로 넣어준다.
- 권한은 `ROLE_`을 앞에 꼭 붙인다. ex)ROLE_USER,ROLE_ADMIN
    ```
    @Component
    @Service("userDetailsService")
    public class UserDetailsServiceImpl implements UserDetailsService {
    
        @Autowired
        private AccountRepository accountRepository;
    
        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    
            // 사용자에게 입력받은 이메일을 이용해서 계정을 찾는다.
            Account account = this.accountRepository.findFirstByEmail(email);
    
            // 계정이 존재하지 않는다면 예외를 발생시킨다.
            if(account == null ){
                throw new UsernameNotFoundException(email + "에 해당하는 계정이 존재하지 않습니다.");
            }
    
            // 계정에서 권한 정보를 추출한 후, GrantedAuthority 컬렉션으로 만든다.
            List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(account.getRole().toString()));
    
            // 기존에 만들어 놨던 UserDetail 의 구현체를 생성 후, 반환한다.
            AccountContext context = new AccountContext(account, roles);
            return context;
            // UserDetailsService(Interface)를 이용하는 객체는
            // 로그인의 가능여부(비밀번호 체크)를 하는 AuthenticationProvider(Interface)의 구현체
        }
    }
    ```
### 3. AuthenticationProvider
- `FilterChainProxy`에게 실질적으로 인증에 관한 작업을 위임받는 객체
- `UserDetailsService`를 이용해 DB에서 사용자 정보를 가져온 후 `PasswordEncoder`를 이용해 평문비밀번호(사용자가 입력한 비밀번호)와 
인코딩된 비밀번호(DB에 저장되어 있는 비밀번호)를 비교한다.
- 패스워드가 일치한다면 `UsernamePasswordAuthenticationToken`를 반환한다.
- 일치하지 않는다면 예외를 발생시킨다.
    ```
        @Component
        public class AuthenticationProviderImpl implements AuthenticationProvider {
        
            // UserDetailsServiceImpl 에서 빈의 이름을 지정하지 않았다면 error 발생
            // UserDetailsServiceImpl 를 타입으로 바로 주입받아도 되지만 이쁘지 않다.
            @Autowired
            private UserDetailsService userDetailsService;
            @Autowired
            private PasswordEncoder passwordEncoder;
        
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                // 전달받은 Authentication 객체에는 사용자로부터 입력받은 FormData(username,password)가 들어있다.
                String email = (String) authentication.getPrincipal(); // 사용자가 입력한 패스워드가 아닌 값 (아이디,이메일 등등)
                String rawPassword = (String) authentication.getCredentials(); // 사용자가 폼에 입력한 패스워드(인코딩되지 않은 평문)
        
                AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(email);
                // UserDetailsServiceImpl가 빈으로 주입되기 때문에 반환타입은 UserDetail 의 구현체이다.
                // (정확히는 UserDetail의 서브인터페이스 User의 구현체)
        
                Account account = accountContext.getAccount();// Context에서 Account(입력받은 이메일로 찾은 계정)
        
        
                // PasswordEncoder의 matcher()메서드에는 평문 비밀번호, 인코딩된 비밀번호 순으로 들어간다.
                // 서로 같지 않을 경우에는 예외를 발생시킨다.
                if(!passwordEncoder.matches(rawPassword, account.getPassword())){
                    throw new BadCredentialsException("로그인 실패!");
                }
                // 패스워드가 일치한다면 토큰을 만든 후, 리턴!
                return new UsernamePasswordAuthenticationToken(accountContext, null, accountContext.getAuthorities());
                // 1번 인자에 계정 정보를 담고 있는 UserDetail 구현체를 넣어준다. (시큐리티 내부에서 인증작업에 계속 사용된다.)
                // 2번 인자에는 계정의 패스워드를 넣어준다. 비밀번호는 더이상 사용하기보다는 필요할 때, 다시 입력받으면 되기 때문에 비밀번호를 따로 넣지 않는다.
                // 3번 인자에는 해당 계정의 권한을 넣어준다.
            }
        
            @Override
            public boolean supports(Class<?> authentication) {
                // Spring Security 가 기본적으로 지원해주는 인증토큰 구현과정이 복잡해서 지원해주는 클래스를 이용
                return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
            }
        }
    ```

### 4.AuthenticationSuccessHandler
- `AuthenticationProvider`에서 인증이 성공하고 후속처리를 하는 핸들러
- HttpServletRequest,Response를 파라미터로 전달받으므로, 여러가지 후속처리가 가능하다.
- 나는 기본적으로 사용자가 로그인 페이지로 넘어오기 전으로 다시 보내주는 작업을 처리한다.
- `HttpSessionRequestCache`를 이용해서 사용자의 이전 URL을 가져올 수 있다.
- 사용자가 만약 외부사이이트에서 바로 접속했다면 null이기 때문에 null일 경우에는 루트 페이지로 보낸다.
    ```
    @RequiredArgsConstructor
    // provider에서 인증작업이 성공할 경우에 실행되는 객체
    // 로그인 기록을 남기거나 여러가지 부가작업을 설정할 수 있다.
    // 기본적으로 유저가 로그인에 다이렉트 되기 전의 페이지로 돌려보내는 작업을 했다.
    public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    // 접속 -> 로그인이 필요한 페이지 -> 로그인 화면으로 강제 이동 -> 로그인 성공 -> 원래 들어가려고 했던 페이지로 보내준다.
    private RequestCache requestCache = new HttpSessionRequestCache();
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
    , Authentication authentication) throws IOException, ServletException {
    // 사용자가 원
    String redirectUrl = getRedirectUrl(request, response);
    response.sendRedirect(redirectUrl);
    }
  
        private String getRedirectUrl(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
            SavedRequest cacheRequest = requestCache.getRequest(request, response);
            // 바로 로그인 화면으로 들어온 경우에는 null 이기 때문에 기본 페이지로 보내준다.
            if(cacheRequest == null || cacheRequest.getRedirectUrl() == null){
                return "/";
            }
            return cacheRequest.getRedirectUrl();
        }
    }
    ```
### 5. PasswordEncoder
- 사용자가 지정한 비밀번호를 알아볼 수 없는 비밀번호로 변경하는 객체
- 관리자 역시 사용자가 입력한 비밀번호를 별도의 방식이 없다면 확인할 수 없다.(암호화된 비밀번호가 저장됨)
- `PasswordEncoder` 역시 인터페이스이므로 여러가지 구현체가 존재한다.(알고리즘 종류에 따라서 분류)
- `PasswordEncoderFactories`를 통해서 생성한다면 `Bcypte`로 형식으로 구현된 클래스를 반환
    ```
            @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    ```

### 6. SecurityContextHolder
- 전역으로 접근가능하며 접근하는 사용자의 세션정보를 통해 인증된 사용자일 경우 Account객체를 받아 올 수 있다.
- Account객체를 받아 올 수 있는 이유는 AccountContext에 Account 객체를 필드변수로 갖고있기 때문이다.
- Controller에서 요청을 받을 때, `@AuthenticationPrincipal`를 이용하면 바로 접근하는 유저의 정보를 파라미터로 받을수 있다.
    ```
        @GetMapping("/accounts")
        public String mypage(@AuthenticationPrincipal User user) {
        // someting...
        
            return ...
         }
    ```
### 7. `Authenticated`
- AuthenticationPrincipal를 이용해서 이용해서 바로 Account 객체를 받을 수 있다.
    ```
        @Retention(value = RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
    public @interface Authenticated {
    
    }
    
    ```
### 8. AccessDeniedExceptionHandler
- Spring Security가 `권한심사과정`에서 발생한 `AccessDeniedException`을 처리하는 핸들러
- FilterChanProxy에서 doFilter를 돌리는 과정에서, `FilterSecurityInterceptor`(권한)에서 발생한 에러를 예외를 처리하는 거 같다. 
    ```
        public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response
                , AccessDeniedException accessDeniedException) throws IOException, ServletException {
    
            response.sendRedirect("/error/403");
        }
    }
    ```