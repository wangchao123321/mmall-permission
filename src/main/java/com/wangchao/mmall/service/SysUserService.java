package com.wangchao.mmall.service;

import com.wangchao.mmall.beans.PageQuery;
import com.wangchao.mmall.beans.PageResult;
import com.wangchao.mmall.model.SysUser;
import com.wangchao.mmall.param.UserParam;

import java.util.List;

public interface SysUserService {

    void save(UserParam param);

    void update(UserParam param);

    SysUser findByKeyWord(String keyword);

    PageResult<SysUser> getPageByDeptId(int deptId, PageQuery pageQuery);

    List<SysUser> getAll();
}
