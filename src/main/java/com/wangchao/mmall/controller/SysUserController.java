package com.wangchao.mmall.controller;

import com.wangchao.mmall.common.JsonData;
import com.wangchao.mmall.param.DeptParam;
import com.wangchao.mmall.param.UserParam;
import com.wangchao.mmall.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {


    @Autowired
    private SysUserService sysUserService;

    @RequestMapping("/saveUser.json")
    @ResponseBody
    public JsonData saveUser(UserParam userParam){
        sysUserService.save(userParam);
        return JsonData.success();
    }

    @RequestMapping("/updateUser.json")
    @ResponseBody
    public JsonData updateUser(UserParam userParam){
        sysUserService.save(userParam);
        return JsonData.success();
    }
}