package com.wangchao.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {


    @RequestMapping("/index.jsp")
    public ModelAndView index(){
        return new ModelAndView("admin");
    }

}
