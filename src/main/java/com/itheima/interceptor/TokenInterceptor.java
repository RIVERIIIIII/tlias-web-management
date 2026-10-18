package com.itheima.interceptor;

import com.itheima.exception.JwtSessionUnavailableException;
import com.itheima.service.JwtSessionService;
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
    private final JwtSessionService jwtSessionService;

    public TokenInterceptor(JwtUtils jwtUtils, JwtSessionService jwtSessionService) {
        this.jwtUtils = jwtUtils;
        this.jwtSessionService = jwtSessionService;
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
            if (!jwtSessionService.isActive(token, claims)) {
                log.info("token会话不存在或已退出，返回401状态码");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            //为aop需要的操作记录获取登录用户id
            CurrentHolder.setCurrentId(Integer.valueOf(claims.get("id").toString()));
        } catch (JwtSessionUnavailableException e) {
            log.warn("JWT会话存储不可用，返回503状态码", e);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return false;
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
