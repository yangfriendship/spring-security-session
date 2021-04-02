package com.security.jwt.repository;

import com.security.jwt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {

    Account findFirstByEmail(String email);

    boolean existsByEmail(String email);

}
