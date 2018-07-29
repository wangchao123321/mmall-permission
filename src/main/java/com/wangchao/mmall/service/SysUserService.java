package com.wangchao.mmall.service;

import com.wangchao.mmall.model.SysUser;
import com.wangchao.mmall.param.UserParam;

public interface SysUserService {

    void save(UserParam param);

    void update(UserParam param);

    SysUser findByKeyWord(String keyword);
}
