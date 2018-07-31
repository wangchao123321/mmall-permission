package com.wangchao.mmall.common;

import com.wangchao.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String url=request.getRequestURI().toString();
        Map requestParameterMap = request.getParameterMap();
        log.info("start url:{},param:{}",url, JsonMapper.objct2String(requestParameterMap));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String url=request.getRequestURI().toString();
        Map requestParameterMap = request.getParameterMap();
        log.info("finish url:{},param:{}",url, JsonMapper.objct2String(requestParameterMap));
        removeThreadLocal();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url=request.getRequestURI().toString();
        Map requestParameterMap = request.getParameterMap();
        log.info("exception url:{},param:{}",url, JsonMapper.objct2String(requestParameterMap));
        removeThreadLocal();
    }

    public void removeThreadLocal(){
        RequestHolder.remove();
    }
}
