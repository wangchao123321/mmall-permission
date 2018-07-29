package com.wangchao.mmall.service;

import com.google.common.base.Preconditions;
import com.wangchao.mmall.dao.SysUserMapper;
import com.wangchao.mmall.exception.ParamException;
import com.wangchao.mmall.model.SysUser;
import com.wangchao.mmall.param.UserParam;
import com.wangchao.mmall.util.BeanValidator;
import com.wangchao.mmall.util.MD5Util;
import com.wangchao.mmall.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void save(UserParam param) {
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }

        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已被占用");
        }

        String password = PasswordUtil.randomPassword();
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser user = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .password(encryptedPassword).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator("system");
        user.setOperatorIp("127.0.0.1");
        user.setOperatorTime(new Date());

        sysUserMapper.insertSelective(user);
    }

    @Override
    public void update(UserParam param) {
        BeanValidator.check(param);
        if(checkTelephoneExist(param.getTelephone(),param.getId())){
            throw new ParamException("电话已被占用");
        }

        if(checkEmailExist(param.getMail(),param.getId())){
            throw new ParamException("邮箱已被占用");
        }
        SysUser before=sysUserMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的用户不存在");
        SysUser after = SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setOperator("system");
        after.setOperatorIp("127.0.0.1");
        after.setOperatorTime(new Date());

        sysUserMapper.updateByPrimaryKeySelective(after);
    }

    @Override
    public SysUser findByKeyWord(String keyword) {
        return null;
    }


    public boolean checkEmailExist(String email,Integer userId){
        return false;
    }

    public boolean checkTelephoneExist(String telephone,Integer userId){
        return false;
    }
}
