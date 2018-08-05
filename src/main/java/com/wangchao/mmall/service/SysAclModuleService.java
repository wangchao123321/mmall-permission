package com.wangchao.mmall.service;

import com.wangchao.mmall.param.AclModuleParam;

public interface SysAclModuleService {

    void save(AclModuleParam aclModuleParam);

    void update(AclModuleParam aclModuleParam);

    void delete(int aclModuleId);
}
