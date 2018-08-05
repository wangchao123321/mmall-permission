package com.wangchao.mmall.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wangchao.mmall.common.JsonData;
import com.wangchao.mmall.model.SysUser;
import com.wangchao.mmall.param.RoleParam;
import com.wangchao.mmall.service.*;
import com.wangchao.mmall.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysTreeService sysTreeService;

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @Autowired
    private SysRoleAclService sysRoleAclService;
    @Autowired
    private SysUserService sysUserService;


    @RequestMapping("/role.page")
    @ResponseBody
    public ModelAndView page(){
        return new ModelAndView("role");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveRole(RoleParam param){
        sysRoleService.save(param);
        return JsonData.success();
    }


    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateRole(RoleParam param){
        sysRoleService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData list(RoleParam param){
        return JsonData.success(sysRoleService.getAll());
    }

    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData roleTree(@RequestParam("roleId") int roleId){
        return JsonData.success(sysTreeService.roleTree(roleId));
    }

    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData changeAcls(@RequestParam("roleId") int roleId,@RequestParam(value = "aclIds",required = false,defaultValue = "") String aclIds){
        List<Integer> aclIdList= StringUtil.splitToListInt(aclIds);
        sysRoleAclService.changeRoleAcls(roleId,aclIdList);
        return JsonData.success();
    }

    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData changeUsers(@RequestParam("roleId") int roleId,@RequestParam(value = "UserIds",required = false,defaultValue = "") String UserIds){
        List<Integer> userIdList= StringUtil.splitToListInt(UserIds);
        sysRoleUserService.changeRoleUsers(roleId,userIdList);
        return JsonData.success();
    }

    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData users(@RequestParam("roleId") int roleId){
        List<SysUser> selectedUserList=sysRoleUserService.getListByRoleId(roleId);
        List<SysUser> allUserList = sysUserService.getAll();
        List<SysUser> unselectedUserList= Lists.newArrayList();

        Set<Integer> selectedUserIdSet=selectedUserList.stream().map(sysUser -> sysUser.getId()).collect(Collectors.toSet());
        for (SysUser sysUser : allUserList) {
            if(sysUser.getStatus()==1 && !selectedUserIdSet.contains(sysUser.getId())){
                unselectedUserList.add(sysUser);
            }
        }
        Map<String,List<SysUser>> map= Maps.newHashMap();
        map.put("selected",selectedUserList);
        map.put("unselected",unselectedUserList);
        return JsonData.success(selectedUserList);
    }
}
