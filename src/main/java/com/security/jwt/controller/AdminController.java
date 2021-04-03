package com.security.jwt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.domain.Account;
import com.security.jwt.domain.dto.AccountDto;
import com.security.jwt.repository.AccountRepository;
import com.security.jwt.security.annotation.RequiredAdmin;
import com.security.jwt.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @GetMapping("/admin")
    @ResponseBody
    @RequiredAdmin
    public String adminIndex(){
        return "admin Page";
    }

    @GetMapping("/admin/users")
    @RequiredAdmin
    @ResponseBody
    public ResponseEntity userList(@RequestBody(required = false)Pageable pageable) throws JsonProcessingException {

        List<Account> list = accountRepository.findAll();
        List<AccountDto> result = new ArrayList<>();

        list.stream().forEach(a -> {
            result.add(modelMapper.map(a,AccountDto.class));
        });

        String listAsString = objectMapper.writeValueAsString(result);

        return ResponseEntity.ok().body(listAsString);
    }


}
