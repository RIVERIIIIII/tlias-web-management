package com.itheima.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
//@WebFilter(urlPatterns = "/*")//指定拦截路径：拦截所有请求
public class demoFilter implements Filter {

    //初始化，在web服务器启动时执行
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        log.info("init 初始化方法 .... ");
    }

    //拦截到请求之后执行，可以执行多次
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("doFilter 拦截到请求 .... ");
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }

    //销毁，在web服务器关闭时执行
    @Override
    public void destroy() {
        Filter.super.destroy();
        log.info("destroy 销毁方法 .... ");
    }
}
