package com.wangchao.mmall.controller;

import com.wangchao.mmall.model.SysUser;
import com.wangchao.mmall.service.SysUserService;
import com.wangchao.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        SysUser sysUser=sysUserService.findByKeyWord(username);

        String errorMsg="";
        String ret=request.getParameter("ret");

        if(StringUtils.isBlank(username)){
            errorMsg = "用户名不可以为空";
        }else if(StringUtils.isBlank(password)){
            errorMsg = "密码不可以为空";
        }else if(sysUser == null){
            errorMsg = "没有该用户";
        }else if(!MD5Util.encrypt(password).equals(sysUser.getPassword())){
            errorMsg = "用户名或密码错误";
        }else if(sysUser.getStatus() != 1){
            errorMsg = "用户已被冻结";
        }else {
            request.getSession().setAttribute("user",sysUser);
            if(StringUtils.isNotBlank(ret)){
                response.sendRedirect(ret);
            }else{
                response.sendRedirect("/admin/index.page");
            }
        }

        request.setAttribute("error",errorMsg);
        request.setAttribute("username",username);
        if(StringUtils.isNotBlank(ret)){
            request.setAttribute("ret",ret);
        }
        request.getRequestDispatcher("signin.jsp").forward(request,response);
    }


    @RequestMapping("/loginOut.page")
    public void loginOut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getSession().invalidate();
        response.sendRedirect("signin.jsp");
    }
}
