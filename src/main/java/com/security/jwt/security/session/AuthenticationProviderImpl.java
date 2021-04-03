package com.security.jwt.security.session;

import com.security.jwt.domain.Account;
import com.security.jwt.security.AccountContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
