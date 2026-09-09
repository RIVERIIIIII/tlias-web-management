package com.itheima.filter;

import com.itheima.utils.JwtUtils;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

//@WebFilter(urlPatterns = "/*")
@Slf4j
public class tokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取请求路径
        String path = request.getRequestURI();//获取URI如：/emps
        //2.判断请求路径是否为登录路径
        if ("/login".equals(path)) {
            log.info("登录操作，放行");
            filterChain.doFilter(request, response);
            return;
        }
        //3.获取请求头中的 token
        String token = request.getHeader("token");
        //4.判断token是否存在
        if (token == null || token.isEmpty()) {
            log.info("token不存在，返回401状态码");
            response.setStatus(401);
            return;
        }
        //5.验证token
        try {
            JwtUtils.parseJWT(token);
        } catch (Exception e) {
            log.info("token非法，返回401状态码");
            response.setStatus(401);
            return;
        }
        //6.放行
        log.info("token合法，放行");
        filterChain.doFilter(request, response);
        return;
    }
}
