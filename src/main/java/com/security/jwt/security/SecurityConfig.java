package com.security.jwt.security;

import com.security.jwt.security.handler.AccessDeniedHandlerImpl;
import com.security.jwt.security.handler.AuthenticationFailureHandlerImpl;
import com.security.jwt.security.handler.AuthenticationSuccessHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] PERMIT_ALL_PATHS = {"/","/login","/sign-up","/error/**"};

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new AuthenticationSuccessHandlerImpl();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return new AuthenticationFailureHandlerImpl();
    }

    public AccessDeniedHandler accessDeniedHandler(){
        return new AccessDeniedHandlerImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .mvcMatchers(PERMIT_ALL_PATHS)
                .permitAll()
                .anyRequest()
                .authenticated()
                ;
        http.formLogin()
                .usernameParameter("email") // 기본값은 username이기 때문에 email로 변경해준다.
                .passwordParameter("password")
                .loginPage("/login")    //Security가 지원하는 로그인 페이지를 이용하지 않고 직접 만든 페이지의 패스를 넣는다.
                .loginProcessingUrl("/login")   // form 데이터의 action에 해당하는 부분 post로 받는다.
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                ;

    }
}
