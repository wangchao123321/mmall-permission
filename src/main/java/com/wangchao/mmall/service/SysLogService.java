package com.wangchao.mmall.service;

import com.wangchao.mmall.beans.PageQuery;
import com.wangchao.mmall.beans.PageResult;
import com.wangchao.mmall.model.*;
import com.wangchao.mmall.param.SearchLogParam;

import java.util.List;

public interface SysLogService {

    public void saveDeptLog(SysDept before, SysDept after);

    public void saveUserLog(SysUser before, SysUser after);

    public void saveAclModuleLog(SysAclModule before, SysAclModule after);

    public void saveAclLog(SysAcl before, SysAcl after);

    public void saveRoleLog(SysRole before, SysRole after);





    PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery pageQuery);

    void recover(int id);

}
