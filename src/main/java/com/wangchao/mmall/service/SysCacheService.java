package com.wangchao.mmall.service;

import com.wangchao.mmall.beans.CacheKeyConstants;


public interface SysCacheService {

    void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix);

    void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix,String... keys);

    String getFormCache(CacheKeyConstants prefix,String... keys);
}
