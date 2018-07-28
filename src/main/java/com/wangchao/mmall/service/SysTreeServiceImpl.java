package com.wangchao.mmall.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.wangchao.mmall.dao.SysDeptMapper;
import com.wangchao.mmall.dto.DeptLevelDto;
import com.wangchao.mmall.model.SysDept;
import com.wangchao.mmall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeServiceImpl implements SysTreeService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

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
}
