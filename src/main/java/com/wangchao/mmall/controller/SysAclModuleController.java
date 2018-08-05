package com.wangchao.mmall.controller;

import com.wangchao.mmall.common.JsonData;
import com.wangchao.mmall.dto.AclModuleLevelDto;
import com.wangchao.mmall.dto.DeptLevelDto;
import com.wangchao.mmall.param.AclModuleParam;
import com.wangchao.mmall.param.DeptParam;
import com.wangchao.mmall.service.SysAclModuleService;
import com.wangchao.mmall.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/sys/aclModule")
@Slf4j
public class SysAclModuleController {

    @Autowired
    private SysAclModuleService sysAclModuleService;

    @Autowired
    private SysTreeService sysTreeService;

    @RequestMapping("/acl.page")
    public ModelAndView page(){
        return new ModelAndView("acl");
    }


    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAclModule(AclModuleParam aclModuleParam){
        sysAclModuleService.save(aclModuleParam);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAclModule(AclModuleParam aclModuleParam){
        sysAclModuleService.update(aclModuleParam);
        return JsonData.success();
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<AclModuleLevelDto> dtoList = sysTreeService.aclModuleTree();
        for (AclModuleLevelDto aclModuleLevelDto : dtoList) {
            System.out.println(aclModuleLevelDto.toString());
        }
        return JsonData.success(dtoList);
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(@RequestParam("id") int id){
        sysAclModuleService.delete(id);
        return JsonData.success();
    }


}
