package com.wangchao.mmall.service;

import com.google.common.collect.Lists;
import com.wangchao.mmall.beans.CacheKeyConstants;
import com.wangchao.mmall.common.RequestHolder;
import com.wangchao.mmall.dao.SysAclMapper;
import com.wangchao.mmall.dao.SysRoleAclMapper;
import com.wangchao.mmall.dao.SysRoleUserMapper;
import com.wangchao.mmall.model.SysAcl;
import com.wangchao.mmall.model.SysRole;
import com.wangchao.mmall.model.SysUser;
import com.wangchao.mmall.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysCoreServiceImpl implements SysCoreService {

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysCacheService sysCacheService;

    @Override
    public List<SysAcl> getCurrentUserAclList() {
        int userId= RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    @Override
    public List<SysAcl> getCurrentUserAclListFromCache() {
        int userId=RequestHolder.getCurrentUser().getId();
        String cacheValue=sysCacheService.getFormCache(CacheKeyConstants.USER_ACLS,String.valueOf(userId));
        if(StringUtils.isBlank(cacheValue)){
            List<SysAcl> aclList=getCurrentUserAclList();
            if(CollectionUtils.isNotEmpty(aclList)){
                sysCacheService.saveCache(JsonMapper.objct2String(aclList),600,CacheKeyConstants.USER_ACLS,String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.String2Obj(cacheValue, new TypeReference<List<SysAcl>>() {
        });
    }

    @Override
    public List<SysAcl> getRoleAclList(int roleId) {
        List<Integer> aclIdList =sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));
        if(CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }

    @Override
    public List<SysAcl> getUserAclList(int userId) {
        if(isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        List<Integer> userRoleIdList=sysRoleUserMapper.getRoleIdListByUserId(userId);
        if(CollectionUtils.isEmpty(userRoleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> userAclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if(CollectionUtils.isEmpty(userAclIdList)){
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclIdList);
    }

    @Override
    public boolean isSuperAdmin() {
        SysUser sysUser=RequestHolder.getCurrentUser();
        if(sysUser.getMail().contains("admin")){
            return true;
        }
        return false;
    }

    @Override
    public boolean hasUrlAcl(String url) {
        if(isSuperAdmin()){
            return true;
        }
        List<SysAcl> aclList=sysAclMapper.getByUrl(url);
        if(CollectionUtils.isEmpty(aclList)){
            return true;
        }
        List<SysAcl> userAclList=getCurrentUserAclListFromCache();
        Set<Integer> userAclIdSet=userAclList.stream().map(acl -> acl.getId()).collect(Collectors.toSet());
        boolean hasValidAcl=false;
        for (SysAcl sysAcl : aclList) {
            if(sysAcl == null || sysAcl.getStatus() != 1){
                continue;
            }
            hasValidAcl=true;
            if(userAclIdSet.contains(sysAcl.getId())){
                return true;
            }
        }
        if(!hasValidAcl){
            return true;
        }
        return false;
    }
}
