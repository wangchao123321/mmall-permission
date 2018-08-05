package com.wangchao.mmall.service;

import com.wangchao.mmall.param.DeptParam;

public interface SysDeptService {


    void save(DeptParam param);

    void update(DeptParam param);

    void delete(int deptId);
}
