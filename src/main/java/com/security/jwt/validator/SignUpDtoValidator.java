package com.security.jwt.validator;

import com.security.jwt.domain.dto.SignUpDto;
import com.security.jwt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SignUpDtoValidator implements Validator {

    // SignUpDtoValidator 가 빈으로 등록되기 때문에 DI 가능하다.
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        // 검증할 대상의 타입을 확인
        return clazz.isAssignableFrom(SignUpDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // supports 메서드를 통해서 target 의 타입이 SignUpDto라는 것을 확인한 후 실행하기 때문에
        // 따로 try~catch 를 할 필요없다.!
        SignUpDto signUpDto = (SignUpDto) target;

        // 비밀번호와 확인비밀번호가 일치하지 않는다면 에러에 값을 넣는다.
        if(!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())){
            errors.rejectValue("confirmPassword","password.notMatch","비밀번호가 일치하지 않습니다.");
        }
        if(accountRepository.existsByEmail(signUpDto.getEmail())){
            errors.rejectValue("email","email.duplicated","비밀번호가 일치하지 않습니다.");
        }

    }
}
