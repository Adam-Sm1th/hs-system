package com.tangdeng.hssystem.interceptors;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.tangdeng.hssystem.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        try {
            JWTUtils.verify(token);
            return true;
        } catch (Exception e) {
            response.setStatus(401);//一级权限校验失败LoginInterceptor
            return false;
        }
    }
}
