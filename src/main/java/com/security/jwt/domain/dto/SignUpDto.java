package com.security.jwt.domain.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Getter // ModelMapper, Thymeleaf 는 내부적으로 객체의 getter 메서드를 이용해서 작동되기 때문에 getter는 Service,Repository가 아니라면
        // 그냥 적는게 더 나은 거 같다.
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {

    @Email
    private String email;

    private String password;

    // 프론트 단에서 비밀번호 검사를 하겠지만 자바스크립트가 익숙하지 않아서 여기서 검사..
    private String confirmPassword;

}
