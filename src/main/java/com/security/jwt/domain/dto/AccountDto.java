package com.security.jwt.domain.dto;

import com.security.jwt.domain.Role;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private Long id;

    private String email;

    private Role role;

}
