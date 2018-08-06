package com.wangchao.mmall.service;

import com.google.common.base.Preconditions;
import com.wangchao.mmall.beans.LogType;
import com.wangchao.mmall.beans.PageQuery;
import com.wangchao.mmall.beans.PageResult;
import com.wangchao.mmall.common.RequestHolder;
import com.wangchao.mmall.dao.*;
import com.wangchao.mmall.dto.SearchLogDto;
import com.wangchao.mmall.exception.ParamException;
import com.wangchao.mmall.model.*;
import com.wangchao.mmall.param.SearchLogParam;
import com.wangchao.mmall.util.BeanValidator;
import com.wangchao.mmall.util.IpUtil;
import com.wangchao.mmall.util.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleAclService sysRoleAclService;

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @Override
    public void recover(int id) {
        SysLogWithBLOBs syslog = sysLogMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(syslog, "带还原的记录不存在");
        switch (syslog.getType()) {
            case LogType.TYPE_DEPT:
                SysDept beforeDept=sysDeptMapper.selectByPrimaryKey(syslog.getTargetId());
                Preconditions.checkNotNull(beforeDept, "带还原的记录不存在");
                if(StringUtils.isBlank(syslog.getNewValue()) || StringUtils.isBlank(syslog.getNewValue())){
                    throw new ParamException("新增和删除操作不能还原");
                }
                SysDept afterDept=JsonMapper.String2Obj(syslog.getOldValue(), new TypeReference<SysDept>() {
                });
                sysDeptMapper.updateByPrimaryKeySelective(afterDept);
                saveDeptLog(beforeDept,afterDept);
                break;
            case LogType.TYPE_USER:
                SysUser beforeUser=sysUserMapper.selectByPrimaryKey(syslog.getTargetId());
                Preconditions.checkNotNull(beforeUser, "带还原的记录不存在");
                if(StringUtils.isBlank(syslog.getNewValue()) || StringUtils.isBlank(syslog.getNewValue())){
                    throw new ParamException("新增和删除操作不能还原");
                }
                SysUser afterUser=JsonMapper.String2Obj(syslog.getOldValue(), new TypeReference<SysUser>() {
                });
                sysUserMapper.updateByPrimaryKeySelective(afterUser);
                saveUserLog(beforeUser,afterUser);
                break;
            case LogType.TYPE_ACL_MODULE:
                SysAclModule beforeAclModule=sysAclModuleMapper.selectByPrimaryKey(syslog.getTargetId());
                Preconditions.checkNotNull(beforeAclModule, "带还原的记录不存在");
                if(StringUtils.isBlank(syslog.getNewValue()) || StringUtils.isBlank(syslog.getNewValue())){
                    throw new ParamException("新增和删除操作不能还原");
                }
                SysAclModule afterAclModule=JsonMapper.String2Obj(syslog.getOldValue(), new TypeReference<SysAclModule>() {
                });
                sysAclModuleMapper.updateByPrimaryKeySelective(afterAclModule);
                saveAclModuleLog(beforeAclModule,afterAclModule);
                break;
            case LogType.TYPE_ACL:
                SysAcl beforeAcl=sysAclMapper.selectByPrimaryKey(syslog.getTargetId());
                Preconditions.checkNotNull(beforeAcl, "带还原的记录不存在");
                if(StringUtils.isBlank(syslog.getNewValue()) || StringUtils.isBlank(syslog.getNewValue())){
                    throw new ParamException("新增和删除操作不能还原");
                }
                SysAcl afterAcl=JsonMapper.String2Obj(syslog.getOldValue(), new TypeReference<SysAcl>() {
                });
                sysAclMapper.updateByPrimaryKeySelective(afterAcl);
                saveAclLog(beforeAcl,afterAcl);
                break;
            case LogType.TYPE_ROLE:
                SysRole beforeRole=sysRoleMapper.selectByPrimaryKey(syslog.getTargetId());
                Preconditions.checkNotNull(beforeRole, "带还原的记录不存在");
                if(StringUtils.isBlank(syslog.getNewValue()) || StringUtils.isBlank(syslog.getNewValue())){
                    throw new ParamException("新增和删除操作不能还原");
                }
                SysRole afterRole=JsonMapper.String2Obj(syslog.getOldValue(), new TypeReference<SysRole>() {
                });
                sysRoleMapper.updateByPrimaryKeySelective(afterRole);
                saveRoleLog(beforeRole,afterRole);
                break;
            case LogType.TYPE_ROLE_ACL:
                SysRole aclRole=sysRoleMapper.selectByPrimaryKey(syslog.getTargetId());
                Preconditions.checkNotNull(aclRole, "角色不存在");
                sysRoleAclService.changeRoleAcls(syslog.getTargetId(),JsonMapper.String2Obj(syslog.getOldValue(), new TypeReference<List<Integer>>() {
                }));
                break;
            case LogType.TYPE_ROLE_USER:
                SysRole userRole=sysRoleMapper.selectByPrimaryKey(syslog.getTargetId());
                Preconditions.checkNotNull(userRole, "角色不存在");
                sysRoleUserService.changeRoleUsers(syslog.getTargetId(),JsonMapper.String2Obj(syslog.getOldValue(), new TypeReference<List<Integer>>() {
                }));
                break;
            default:

        }
    }

    @Override
    public void saveDeptLog(SysDept before, SysDept after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_DEPT);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOldValue(before == null ? "" : JsonMapper.objct2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objct2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperator(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    @Override
    public void saveUserLog(SysUser before, SysUser after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOldValue(before == null ? "" : JsonMapper.objct2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objct2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperator(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    @Override
    public void saveAclModuleLog(SysAclModule before, SysAclModule after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL_MODULE);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOldValue(before == null ? "" : JsonMapper.objct2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objct2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperator(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    @Override
    public void saveAclLog(SysAcl before, SysAcl after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOldValue(before == null ? "" : JsonMapper.objct2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objct2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperator(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    @Override
    public void saveRoleLog(SysRole before, SysRole after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOldValue(before == null ? "" : JsonMapper.objct2String(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objct2String(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperator(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }



    @Override
    public PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        SearchLogDto dto = new SearchLogDto();
        dto.setType(param.getType());
        if (StringUtils.isNotBlank(param.getBeforeSeg())) {
            dto.setBeforeSeg("%" + param.getBeforeSeg() + "%");
        }
        if (StringUtils.isNotBlank(param.getAfterSeg())) {
            dto.setAfterSeg("%" + param.getAfterSeg() + "%");
        }
        if (StringUtils.isNotBlank(param.getOperator())) {
            dto.setOperator("%" + param.getOperator() + "%");
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotBlank(param.getFromTime())) {
                dto.setFromTime(format.parse(param.getFromTime()));
            }
            if (StringUtils.isNotBlank(param.getToTime())) {
                dto.setToTime(format.parse(param.getToTime()));
            }
        } catch (Exception e) {

        }

        int count = sysLogMapper.countBySearchDto(dto);
        if (count > 0) {
            List<SysLogWithBLOBs> logList = sysLogMapper.getPageListBySearchDto(dto, pageQuery);
            return PageResult.<SysLogWithBLOBs>builder().total(count).data(logList).build();
        }
        return PageResult.<SysLogWithBLOBs>builder().build();
    }


}
