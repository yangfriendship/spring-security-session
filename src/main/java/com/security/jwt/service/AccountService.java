package com.security.jwt.service;

import com.security.jwt.domain.Account;
import com.security.jwt.domain.dto.SignUpDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountService {

    Account signUp(Account account);

}
