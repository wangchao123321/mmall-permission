package com.wangchao.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.wangchao.mmall.dao.SysAclMapper;
import com.wangchao.mmall.dao.SysAclModuleMapper;
import com.wangchao.mmall.dao.SysDeptMapper;
import com.wangchao.mmall.dto.AclDto;
import com.wangchao.mmall.dto.AclModuleLevelDto;
import com.wangchao.mmall.dto.DeptLevelDto;
import com.wangchao.mmall.model.SysAcl;
import com.wangchao.mmall.model.SysAclModule;
import com.wangchao.mmall.model.SysDept;
import com.wangchao.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysCoreService sysCoreService;

    @Autowired
    private SysAclMapper sysAclMapper;

    public List<AclModuleLevelDto> aclModuleTree(){
        List<SysAclModule> sysAclModules=sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList=Lists.newArrayList();
        for (SysAclModule sysAclModule : sysAclModules) {
            dtoList.add(AclModuleLevelDto.adapt(sysAclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    @Override
    public List<AclModuleLevelDto> roleTree(int roleId) {
        // 1 当前用户已分配的权限点
        List<SysAcl> userAclList=sysCoreService.getCurrentUserAclList();
        // 2 当前角色分配的权限点
        List<SysAcl> roleAclList=sysCoreService.getRoleAclList(roleId);
        // 3 当前系统所有权限点
        List<AclDto> aclDtoList=Lists.newArrayList();

        Set<Integer> userAclIdSet=userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet=roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList=sysAclMapper.getAll();
        Set<SysAcl> aclSet=new HashSet<>(allAclList);

        for (SysAcl sysAcl : aclSet) {
            AclDto dto=AclDto.adapt(sysAcl);
            if(userAclIdSet.contains(sysAcl.getId())){
                dto.setHasAcl(true);
            }
            if(roleAclIdSet.contains(sysAcl.getId())){
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if(CollectionUtils.isEmpty(aclDtoList)){
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelDtos=aclModuleTree();

        Multimap<Integer,AclDto> moduleIdAclMap=ArrayListMultimap.create();
        for (AclDto aclDto : aclDtoList) {
            if(aclDto.getStatus() == 1){
                moduleIdAclMap.put(aclDto.getAciModuleId(),aclDto);
            }
        }
        bindAclsWithOrder(aclModuleLevelDtos,moduleIdAclMap);
        return aclModuleLevelDtos;
    }

    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelDtos,Multimap<Integer,AclDto> moduleIdAclMap){
        if(CollectionUtils.isEmpty(aclModuleLevelDtos)){
            return;
        }
        for (AclModuleLevelDto aclModuleLevelDto : aclModuleLevelDtos) {
            List<AclDto> aclDtoList= (List<AclDto>) moduleIdAclMap.get(aclModuleLevelDto.getId());
            if(CollectionUtils.isNotEmpty(aclDtoList)){
                Collections.sort(aclDtoList,aclSeqComparator);
                aclModuleLevelDto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(aclModuleLevelDto.getAclModuleList(),moduleIdAclMap);
        }
    }


    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }
        Multimap<String,AclModuleLevelDto> aclModuleLevelDtoMultimap= ArrayListMultimap.create();

        List<AclModuleLevelDto> rootList=Lists.newArrayList();

        for (AclModuleLevelDto aclModuleLevelDto : dtoList) {
            aclModuleLevelDtoMultimap.put(aclModuleLevelDto.getLevel(),aclModuleLevelDto);
            if(LevelUtil.ROOT.equals(aclModuleLevelDto.getLevel())){
                rootList.add(aclModuleLevelDto);
            }
        }
        Collections.sort(rootList,aclModuleLevelDtoComparator);
        transformAclModuleTree(rootList,LevelUtil.ROOT,aclModuleLevelDtoMultimap);
        return rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> rootList,String level,Multimap<String,AclModuleLevelDto> aclModuleLevelDtoMultimap ){
        for (int i = 0; i < rootList.size(); i++) {
            AclModuleLevelDto dto=rootList.get(i);
            String nextLevle=LevelUtil.calculateLevel(level,dto.getId());
            List<AclModuleLevelDto> tempList= (List<AclModuleLevelDto>) aclModuleLevelDtoMultimap.get(nextLevle);
            if(CollectionUtils.isNotEmpty(tempList)){
                Collections.sort(rootList,aclModuleLevelDtoComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList,nextLevle,aclModuleLevelDtoMultimap);
            }
        }
    }

    public List<DeptLevelDto> deptTree(){
        List<SysDept> depts=sysDeptMapper.getAllDept();
        List<DeptLevelDto> deptLevelDtoList= Lists.newArrayList();
        for (SysDept dept : depts) {
            DeptLevelDto deptLevelDto=DeptLevelDto.adapt(dept);
            deptLevelDtoList.add(deptLevelDto);
        }
        return deptLevelDtoList;
    }

    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelDtoList){
        if(CollectionUtils.isEmpty(deptLevelDtoList)){
            return Lists.newArrayList();
        }
        Multimap<String,DeptLevelDto> levelDtoMultimap= ArrayListMultimap.create();

        List<DeptLevelDto> rootList=Lists.newArrayList();

        for (DeptLevelDto deptLevelDto : deptLevelDtoList) {
            levelDtoMultimap.put(deptLevelDto.getLevel(),deptLevelDto);
            if(LevelUtil.ROOT.equals(deptLevelDto.getLevel())){
                rootList.add(deptLevelDto);
            }
        }

        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        transformDeptTree(deptLevelDtoList,LevelUtil.ROOT,levelDtoMultimap);
        return rootList;
    }

    public void transformDeptTree(List<DeptLevelDto> deptLevelDtoList,String level,Multimap<String,DeptLevelDto> levelDtoMultimap){
        for (int i = 0; i < deptLevelDtoList.size(); i++) {
            // 遍历该层的每个元素
            DeptLevelDto deptLevelDto=deptLevelDtoList.get(i);
            // 处理当前层级的数据
            String nextLevel=LevelUtil.calculateLevel(level,deptLevelDto.getId());
            // 处理下一层
            List<DeptLevelDto> tempDeptList= (List<DeptLevelDto>) levelDtoMultimap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)){
                // 排序
                Collections.sort(tempDeptList,deptLevelDtoComparator);
                // 设置下一个部门
                deptLevelDto.setDeptList(tempDeptList);
                // 进入到下一层处理
                transformDeptTree(tempDeptList,nextLevel,levelDtoMultimap);
            }
        }
    }

    public Comparator<DeptLevelDto> deptLevelDtoComparator=new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto> aclModuleLevelDtoComparator=new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public Comparator<AclDto> aclSeqComparator=new Comparator<AclDto>() {
        @Override
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
}
