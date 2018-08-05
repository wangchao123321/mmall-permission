package com.wangchao.mmall.controller;

import com.wangchao.mmall.beans.PageQuery;
import com.wangchao.mmall.common.JsonData;
import com.wangchao.mmall.model.*;
import com.wangchao.mmall.param.SearchLogParam;
import com.wangchao.mmall.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/sys/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;


    @RequestMapping("/recover.json")
    @ResponseBody
    public JsonData recover(@RequestParam("id") int id){
        return JsonData.success(sysLogService.searchPageList(param,pageQuery));
    }

    @RequestMapping("/log.page")
    public ModelAndView page(){
        return new ModelAndView("log");
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(SearchLogParam param, PageQuery pageQuery){
        return JsonData.success(sysLogService.searchPageList(param,pageQuery));
    }
}
