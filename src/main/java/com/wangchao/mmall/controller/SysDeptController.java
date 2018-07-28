package com.wangchao.mmall.controller;

import com.wangchao.mmall.common.JsonData;
import com.wangchao.mmall.dto.DeptLevelDto;
import com.wangchao.mmall.param.DeptParam;
import com.wangchao.mmall.service.SysDeptService;
import com.wangchao.mmall.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {


    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysTreeService sysTreeService;

    @RequestMapping("/saveDept.json")
    @ResponseBody
    public JsonData saveDept(DeptParam deptParam){
        sysDeptService.save(deptParam);
        return JsonData.success();
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<DeptLevelDto> dtoList=sysTreeService.deptTree();
        return JsonData.success(dtoList);
    }


    @RequestMapping("/updateDept.json")
    @ResponseBody
    public JsonData updateDept(DeptParam deptParam){
        sysDeptService.update(deptParam);
        return JsonData.success();
    }

}
