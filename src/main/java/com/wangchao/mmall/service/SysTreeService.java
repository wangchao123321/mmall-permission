package com.wangchao.mmall.service;

import com.wangchao.mmall.dto.DeptLevelDto;

import java.util.List;

public interface SysTreeService {

    public List<DeptLevelDto> deptTree();

    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelDtoList);
}
