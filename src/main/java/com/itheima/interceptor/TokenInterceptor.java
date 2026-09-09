package com.itheima.interceptor;

import com.itheima.utils.CurrentHolder;
import com.itheima.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;

    public TokenInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在配置文件的拦截器中配置排除"/login"路径
        /*//1.获取请求路径
        String path = request.getRequestURI();//获取URI如：/emps
        //2.判断请求路径是否为登录路径
        if ("/login".equals(path)) {
            log.info("登录操作，放行");
            return  true;
        }*/
        //3.获取请求头中的 token
        String token = request.getHeader("token");
        //4.判断token是否存在
        if (token == null || token.isEmpty()) {
            log.info("token不存在，返回401状态码");
            response.setStatus(401);
            return false;
        }
        //5.验证token
        try {
            Claims claims = jwtUtils.parseJWT(token);
            //为aop需要的操作记录获取登录用户id
            CurrentHolder.setCurrentId(Integer.valueOf(claims.get("id").toString()));
        } catch (Exception e) {
            log.info("token非法，返回401状态码");
            response.setStatus(401);
            return false;
        }
        //6.放行
        log.info("token合法，放行");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentHolder.remove();
    }
}
