package com.wangchao.mmall.common;

import com.wangchao.mmall.exception.ParamException;
import com.wangchao.mmall.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {

        String url=request.getRequestURL().toString();

        ModelAndView modelAndView=null;

        String errorDefault="System error";

        // .json .page
        if(url.endsWith(".json")){
            if(e instanceof PermissionException || e instanceof ParamException){
                JsonData jsonData=JsonData.fail(e.getMessage());
                modelAndView=new ModelAndView("jsonView",jsonData.toMap());
            }else{
                JsonData jsonData=JsonData.fail(errorDefault);
                modelAndView=new ModelAndView("jsonView",jsonData.toMap());
            }
        }else if(url.startsWith(".page")){
            JsonData jsonData=JsonData.fail(errorDefault);
            modelAndView=new ModelAndView("exception",jsonData.toMap());
        }else{
            JsonData jsonData=JsonData.fail(errorDefault);
            modelAndView=new ModelAndView("jsonView",jsonData.toMap());
        }


        return modelAndView;
    }
}
