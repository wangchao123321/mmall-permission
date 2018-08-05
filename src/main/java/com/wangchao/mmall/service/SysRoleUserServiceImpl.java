package com.wangchao.mmall.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wangchao.mmall.common.RequestHolder;
import com.wangchao.mmall.dao.SysRoleUserMapper;
import com.wangchao.mmall.dao.SysUserMapper;
import com.wangchao.mmall.model.SysRoleUser;
import com.wangchao.mmall.model.SysUser;
import com.wangchao.mmall.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleUserServiceImpl implements SysRoleUserService {


    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLogService sysLogService;

    @Override
    public List<SysUser> getListByRoleId(int roleId) {
        List<Integer> userIdListByRoleId = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdListByRoleId)){
            return Lists.newArrayList();
        }
        return null;
    }

    @Override
    public void changeRoleUsers(int roleId, List<Integer> userIdList) {
        List<Integer> originUserIdList=sysRoleUserMapper.getUserIdListByRoleId(roleId);
        if(originUserIdList.size() == userIdList.size()){
            Set<Integer> originUserIdSet= Sets.newHashSet(originUserIdList);
            Set<Integer> userIdSet=Sets.newHashSet(userIdList);
            originUserIdSet.removeAll(userIdSet);
            if(CollectionUtils.isEmpty(originUserIdSet)){
                return;
            }
        }
        updateRoleUsers(roleId,userIdList);
        sysLogService.saveRoleUserLog(roleId,originUserIdList,userIdList);
    }

    @Transactional
    private void updateRoleUsers(int roleId, List<Integer> userIdList) {
        sysRoleUserMapper.deleteByRoleId(roleId);
        if(CollectionUtils.isEmpty(userIdList)){
            return;
        }
        List<SysRoleUser> roleUserList=Lists.newArrayList();
        for (Integer userId : userIdList) {
            SysRoleUser sysRoleUser=SysRoleUser.builder().roleId(roleId).userId(userId)
                    .operatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operatorTime(new Date()).build();
            roleUserList.add(sysRoleUser);
        }
        sysRoleUserMapper.batchInsert(roleUserList);
    }
}
