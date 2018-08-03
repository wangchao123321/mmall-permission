package com.wangchao.mmall.service;

import com.wangchao.mmall.dto.AclModuleLevelDto;
import com.wangchao.mmall.dto.DeptLevelDto;

import java.util.List;

public interface SysTreeService {

    List<DeptLevelDto> deptTree();

    List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelDtoList);

    List<AclModuleLevelDto> aclModuleTree();

    List<AclModuleLevelDto> roleTree(int roleId);
}
