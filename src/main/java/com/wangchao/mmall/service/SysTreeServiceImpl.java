package com.wangchao.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.wangchao.mmall.dao.SysAclModuleMapper;
import com.wangchao.mmall.dao.SysDeptMapper;
import com.wangchao.mmall.dto.AclModuleLevelDto;
import com.wangchao.mmall.dto.DeptLevelDto;
import com.wangchao.mmall.model.SysAclModule;
import com.wangchao.mmall.model.SysDept;
import com.wangchao.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    public List<AclModuleLevelDto> aclModuleTree(){
        List<SysAclModule> sysAclModules=sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList=Lists.newArrayList();
        for (SysAclModule sysAclModule : sysAclModules) {
            dtoList.add(AclModuleLevelDto.adapt(sysAclModule));
        }
        return aclModuleListToTree(dtoList);
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
}
