package com.wangchao.mmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wangchao.mmall.beans.LogType;
import com.wangchao.mmall.common.RequestHolder;
import com.wangchao.mmall.dao.SysLogMapper;
import com.wangchao.mmall.dao.SysRoleAclMapper;
import com.wangchao.mmall.model.SysLogWithBLOBs;
import com.wangchao.mmall.model.SysRoleAcl;
import com.wangchao.mmall.util.IpUtil;
import com.wangchao.mmall.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleAclServiceImpl implements SysRoleAclService{

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysLogMapper sysLogMapper;


    @Override
    public void changeRoleAcls(Integer roleId, List<Integer> aclIdList) {
        List<Integer> originAclIdList=sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if(originAclIdList.size() == aclIdList.size()){
            Set<Integer> originAclIdSet= Sets.newHashSet(originAclIdList);
            Set<Integer> aclIdSet=Sets.newHashSet(aclIdList);
            originAclIdSet.removeAll(aclIdSet);
            if(CollectionUtils.isEmpty(originAclIdSet)){
                return;
            }
        }
        updateRoleAcls(roleId,aclIdList);
        saveRoleAclLog(roleId,originAclIdList,aclIdList);
    }

    @Transactional
    public void updateRoleAcls(int roleId,List<Integer> aclIdList){
        sysRoleAclMapper.deleteByRoleId(roleId);

        if(CollectionUtils.isEmpty(aclIdList)){
            return;
        }
        List<SysRoleAcl> roleAclList=Lists.newArrayList();
        for (Integer aclId : aclIdList) {
            SysRoleAcl roleAcl=SysRoleAcl.builder().roleId(roleId).aclId(aclId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operatorTime(new Date()).build();
            roleAclList.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }

    @Override
    public void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objct2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objct2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperator(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}
