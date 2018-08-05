package com.wangchao.mmall.service;

import com.google.common.base.Preconditions;
import com.wangchao.mmall.common.RequestHolder;
import com.wangchao.mmall.dao.SysAclMapper;
import com.wangchao.mmall.dao.SysAclModuleMapper;
import com.wangchao.mmall.exception.ParamException;
import com.wangchao.mmall.model.SysAclModule;
import com.wangchao.mmall.model.SysDept;
import com.wangchao.mmall.param.AclModuleParam;
import com.wangchao.mmall.util.BeanValidator;
import com.wangchao.mmall.util.IpUtil;
import com.wangchao.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleServiceImpl implements SysAclModuleService {

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysLogService sysLogService;

    @Override
    public void save(AclModuleParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule after=SysAclModule.builder().name(param.getName()).parentId(param.getParentId()).seq(param.getSeq())
                .status(param.getStatus()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysAclModuleMapper.insertSelective(after);
        sysLogService.saveAclModuleLog(null,after);
    }

    @Override
    public void update(AclModuleParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }

        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的权限模块不存在");
        SysAclModule after=SysAclModule.builder().name(param.getName()).parentId(param.getParentId()).seq(param.getSeq())
                .status(param.getStatus()).remark(param.getRemark()).id(param.getId()).build();

        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());

        updateWithChild(before,after);
        sysLogService.saveAclModuleLog(before,after);
    }

    @Override
    public void delete(int aclModuleId) {
        SysAclModule aclModule=sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        Preconditions.checkNotNull(aclModule,"待删除的权限模块不存在,无法删除");
        if(sysAclModuleMapper.countByParentId(aclModule.getId()) > 0){
            throw new ParamException("当前模块下面有子部门，无法删除");
        }
        if(sysAclMapper.countByAclModuleId(aclModule.getId()) >0){
            throw new ParamException("当前模块下面有用户，无法删除");
        }
        sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);
    }

    @Transactional
    void updateWithChild(SysAclModule before, SysAclModule after){
        String newLevelPrefix=after.getLevel();
        String oldLevelPrefix=before.getLevel();

        if(!after.getLevel().equals(before.getLevel())){
            List<SysAclModule> sysAclModules=sysAclModuleMapper.getChildDeptListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(sysAclModules)){
                for (SysAclModule aclModule : sysAclModules) {
                    String level=aclModule.getLevel();
                    if(level.indexOf(oldLevelPrefix) == 0){
                        level = newLevelPrefix+level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(sysAclModules);
            }
        }
        sysAclModuleMapper.updateByPrimaryKey(after);
    }

    private boolean checkExist(Integer parentId,String aclModelName,Integer deptId){
        return sysAclModuleMapper.countByNameAndParentId(parentId,aclModelName,deptId) > 0;
    }

    private String getLevel(Integer aclModuleId){
        SysAclModule sysAclModule=sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if(sysAclModule == null){
            return null;
        }
        return sysAclModule.getLevel();
    }
}
