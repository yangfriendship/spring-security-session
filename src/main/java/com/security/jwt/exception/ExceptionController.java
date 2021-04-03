package com.security.jwt.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ExceptionController {

    @GetMapping("/403")
    public String error403(){
        return "error/403";
    }
}