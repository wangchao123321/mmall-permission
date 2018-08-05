package com.wangchao.mmall.service;

import com.google.common.base.Preconditions;
import com.wangchao.mmall.beans.PageQuery;
import com.wangchao.mmall.beans.PageResult;
import com.wangchao.mmall.common.RequestHolder;
import com.wangchao.mmall.dao.SysAclMapper;
import com.wangchao.mmall.exception.ParamException;
import com.wangchao.mmall.model.SysAcl;
import com.wangchao.mmall.param.AclParam;
import com.wangchao.mmall.util.BeanValidator;
import com.wangchao.mmall.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclServiceImpl implements SysAclService{

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysLogService sysLogService;

    @Override
    public void save(AclParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl acl = SysAcl.builder().name(param.getName()).aciModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();
//        acl.setCode(generateCode());
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperatorTime(new Date());
        acl.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAclMapper.insertSelective(acl);
        sysLogService.saveAclLog(null, acl);
    }

    @Override
    public void update(AclParam param) {
        BeanValidator.check(param);
        if(checkExist(param.getAclModuleId(),param.getName(),param.getId())){
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl before = sysAclMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的权限点不存在");

        SysAcl after = SysAcl.builder().id(param.getId()).name(param.getName()).aciModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperatorTime(new Date());
        after.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));

        sysAclMapper.updateByPrimaryKeySelective(after);
        sysLogService.saveAclLog(before, after);
    }

    @Override
    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery query) {
        BeanValidator.check(query);
        int count=sysAclMapper.countByAclModuleId(aclModuleId);
        if(count > 0){
            List<SysAcl> aclList=sysAclMapper.getPageByAclModuleId(aclModuleId,query);
            return PageResult.<SysAcl>builder().data(aclList).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }

    private boolean checkExist(int aclModuleId,String name,Integer id){
        return sysAclMapper.countByNameAndAclModuleId(aclModuleId,name,id) > 0;
    }

    public String generateCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date()) + "_" + (int)(Math.random() * 100);
    }
}
