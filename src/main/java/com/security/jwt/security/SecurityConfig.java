package com.security.jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private static final String[] PERMIT_ALL_PATHS = {"/","/login","/sign-up"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .mvcMatchers(PERMIT_ALL_PATHS)
                .permitAll()
                .anyRequest()
                .authenticated()
                ;


    }
}
