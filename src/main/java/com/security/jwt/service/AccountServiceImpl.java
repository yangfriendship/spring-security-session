package com.security.jwt.service;

import com.security.jwt.domain.Account;
import com.security.jwt.domain.Role;
import com.security.jwt.domain.dto.SignUpDto;
import com.security.jwt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Account signUp(Account account) {
        // 사용자정보를 제외한 가입시간, 권한, 로그인 시간 등등은 서비스단에서 입력한다.
        account.setRole(Role.USER);

        // 평문 비밀번호를 인코딩한 후에 save
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return this.accountRepository.save(account);
    }


}
