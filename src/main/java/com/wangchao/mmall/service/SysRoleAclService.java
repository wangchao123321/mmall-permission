package com.wangchao.mmall.service;

import java.util.List;

public interface SysRoleAclService {

    void changeRoleAcls(Integer roleId, List<Integer> aclIdList);

    public void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after);
}
