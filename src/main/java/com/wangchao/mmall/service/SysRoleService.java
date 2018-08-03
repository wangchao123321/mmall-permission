package com.wangchao.mmall.service;

import com.wangchao.mmall.model.SysRole;
import com.wangchao.mmall.param.RoleParam;

import java.util.List;

public interface SysRoleService {

    void save(RoleParam param);

    void update(RoleParam param);

    List<SysRole> getAll();
}
