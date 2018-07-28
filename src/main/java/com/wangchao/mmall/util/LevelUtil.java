package com.wangchao.mmall.util;

import org.apache.commons.lang3.StringUtils;

public class LevelUtil {

    private static final String SEPARATOR=".";

    public static final String ROOT="0";

    public static String calculateLevel(String parentLevle,int parentId){
        if(StringUtils.isBlank(parentLevle)){
            return ROOT;
        }else{
            return StringUtils.join(parentLevle,SEPARATOR,parentId);
        }
    }
}
