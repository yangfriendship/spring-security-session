package com.security.jwt.security;

import com.security.jwt.domain.Account;
import com.security.jwt.repository.AccountRepository;
import com.sun.tools.javac.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
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
