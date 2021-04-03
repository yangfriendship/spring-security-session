package com.security.jwt.controller;

import com.security.jwt.domain.Account;
import com.security.jwt.domain.dto.SignUpDto;
import com.security.jwt.security.Authenticated;
import com.security.jwt.service.AccountService;
import com.security.jwt.validator.SignUpDtoValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private SignUpDtoValidator signUpDtoValidator;
    @Autowired
    private ModelMapper modelMapper;

    @InitBinder("signUpDto")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpDtoValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model, @Authenticated Account account){
        if(account != null) { // SecurityContextHolder 에 Account가 있다면 로그인 화면으로 들어올 수 없다.
            return "/";
        }
        SignUpDto signUpDto = new SignUpDto();
        // 이름을 설정하지 않으면 카멜케이스로 변형된 이름이 자동으로 설정됨
        model.addAttribute(signUpDto);
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Authenticated Account account,@Valid SignUpDto signUpDto, Errors errors){
        if(account != null) {
            return "/";
        }
        if(errors.hasErrors()) {
            return "account/sign-up";
        }
        Account saved = this.modelMapper.map(signUpDto, Account.class);
        this.accountService.signUp(saved);
        return "redirect:/";
    }

    @GetMapping("/login")
    // 폼 데이터를 받는 Post 요청은 Security에 설정된 Url에서 진행된다.
    public String loginForm(){
        return "account/login";
    }
}
