package com.wangchao.mmall.service;

import com.wangchao.mmall.beans.PageQuery;
import com.wangchao.mmall.beans.PageResult;
import com.wangchao.mmall.model.SysAcl;
import com.wangchao.mmall.param.AclParam;

public interface SysAclService {

    void save(AclParam param);

    void update(AclParam param);

    PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery query);

}
