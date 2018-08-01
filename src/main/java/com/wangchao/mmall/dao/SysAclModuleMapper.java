package com.wangchao.mmall.dao;

import com.wangchao.mmall.model.SysAclModule;
import com.wangchao.mmall.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    int countByNameAndParentId(@Param("parentId") Integer parentId, @Param("name") String name, @Param("id") Integer id);

    List<SysAclModule> getChildDeptListByLevel(@Param("level") String level);

    void batchUpdateLevel(@Param("aclModules") List<SysAclModule> aclModules);

    List<SysAclModule> getAllAclModule();
}