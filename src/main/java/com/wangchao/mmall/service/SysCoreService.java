package com.wangchao.mmall.service;

import com.wangchao.mmall.model.SysAcl;

import java.util.List;

public interface SysCoreService {

    List<SysAcl> getCurrentUserAclList();

    List<SysAcl> getCurrentUserAclListFromCache();

    List<SysAcl> getRoleAclList(int roleId);

    List<SysAcl> getUserAclList(int userId);

    boolean isSuperAdmin();

    boolean hasUrlAcl(String url);


}
