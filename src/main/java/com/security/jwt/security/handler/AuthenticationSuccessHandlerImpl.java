package com.security.jwt.security.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequiredArgsConstructor
// provider에서 인증작업이 성공할 경우에 실행되는 객체
// 로그인 기록을 남기거나 여러가지 부가작업을 설정할 수 있다.
// 기본적으로 유저가 로그인에 다이렉트 되기 전의 페이지로 돌려보내는 작업을 했다.
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    // 접속 -> 로그인이 필요한 페이지 -> 로그인 화면으로 강제 이동 -> 로그인 성공 -> 원래 들어가려고 했던 페이지로 보내준다.
    private RequestCache requestCache = new HttpSessionRequestCache();
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) throws IOException, ServletException {
        // 사용자가 원
        String redirectUrl = getRedirectUrl(request, response);
        response.sendRedirect(redirectUrl);
    }

    private String getRedirectUrl(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        SavedRequest cacheRequest = requestCache.getRequest(request, response);
        // 바로 로그인 화면으로 들어온 경우에는 null 이기 때문에 기본 페이지로 보내준다.
        if(cacheRequest == null || cacheRequest.getRedirectUrl() == null){
            return "/";
        }
        return cacheRequest.getRedirectUrl();
    }
}
