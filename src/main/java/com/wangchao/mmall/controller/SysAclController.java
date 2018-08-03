package com.wangchao.mmall.controller;

import com.wangchao.mmall.beans.PageQuery;
import com.wangchao.mmall.common.JsonData;
import com.wangchao.mmall.param.AclModuleParam;
import com.wangchao.mmall.param.AclParam;
import com.wangchao.mmall.service.SysAclService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {

    @Autowired
    private SysAclService sysAclService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAcl(AclParam aclParam){
        sysAclService.save(aclParam);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAcl(AclParam aclParam){
        sysAclService.update(aclParam);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(@RequestParam("aclModuleId") Integer aclModuleId, PageQuery pageQuery){
        sysAclService.getPageByAclModuleId(aclModuleId,pageQuery);
        return JsonData.success();
    }
}
