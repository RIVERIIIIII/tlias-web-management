package com.itheima.controller;

import com.itheima.pojo.Result;
import com.itheima.service.JwtSessionService;
import com.itheima.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logout")
public class LogoutController {
    private final JwtUtils jwtUtils;
    private final JwtSessionService jwtSessionService;

    public LogoutController(JwtUtils jwtUtils, JwtSessionService jwtSessionService) {
        this.jwtUtils = jwtUtils;
        this.jwtSessionService = jwtSessionService;
    }

    @PostMapping
    public Result logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = jwtUtils.parseJWT(token);
        jwtSessionService.revoke(claims);
        return Result.success();
    }
}
