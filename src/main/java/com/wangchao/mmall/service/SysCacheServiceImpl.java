package com.wangchao.mmall.service;

import com.google.common.base.Joiner;
import com.wangchao.mmall.beans.CacheKeyConstants;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

@Service
public class SysCacheServiceImpl implements SysCacheService {

    @Resource(name = "redisPool")
    private RedisPool redisPool;

    @Override
    public void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix) {
        saveCache(toSavedValue,timeoutSeconds,prefix,null);
    }

    @Override
    public void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix, String... keys) {
        if(toSavedValue==null){
            return;
        }
        ShardedJedis shardedJedis=null;
        try {
            String cacheKey=generateCacheKey(prefix,keys);
            shardedJedis = redisPool.instance();
            shardedJedis.setex(cacheKey,timeoutSeconds,toSavedValue);
        }catch (Exception e){

        }
    }

    @Override
    public String getFormCache(CacheKeyConstants prefix, String... keys) {
        ShardedJedis shardedJedis=null;
        String cacheKey=generateCacheKey(prefix,keys);
        try {
            shardedJedis = redisPool.instance();
            String value = shardedJedis.get(cacheKey);
            return value;
        }catch (Exception e){
            return null;
        }finally {
            redisPool.safeClose(shardedJedis);
        }
    }


    private String generateCacheKey(CacheKeyConstants prefix,String... keys){
        String key=prefix.name();
        if(keys != null && keys.length>0){
            key+="_"+ Joiner.on("_").join(keys);
        }
        return key;
    }
}
