package com.wangchao.mmall.controller;

import com.wangchao.mmall.common.ApplicationContextHealper;
import com.wangchao.mmall.common.JsonData;
import com.wangchao.mmall.dao.SysAclModuleMapper;
import com.wangchao.mmall.dao.SysDeptMapper;
import com.wangchao.mmall.exception.ParamException;
import com.wangchao.mmall.exception.PermissionException;
import com.wangchao.mmall.model.SysAclModule;
import com.wangchao.mmall.param.TestVo;
import com.wangchao.mmall.util.BeanValidator;
import com.wangchao.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello() throws Exception {
//        throw new PermissionException("text exception");
//        SysAclModuleMapper moduleMapper= ApplicationContextHealper.popBean(SysAclModuleMapper.class);
//        SysAclModule sysAclModule = moduleMapper.selectByPrimaryKey(1);
//        System.out.println(JsonMapper.objct2String(sysAclModule));
        sysDeptMapper.getAllDept();
        System.out.println("");
        return null;
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo) throws ParamException {
        try {
           BeanValidator.check(vo);
        }catch (Exception e){

        }
        throw new PermissionException("text validate");
    }



}
