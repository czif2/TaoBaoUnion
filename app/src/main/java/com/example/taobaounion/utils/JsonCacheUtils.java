package com.example.taobaounion.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.taobaounion.base.BaseApplication;
import com.example.taobaounion.model.domain.CacheWithDuration;
import com.google.gson.Gson;

public class JsonCacheUtils {

    private final SharedPreferences mSharedPreferences;
    private final Gson mGson;

    private JsonCacheUtils(){
        mSharedPreferences = BaseApplication.getAppContext().getSharedPreferences("json_cache_sp_name", Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    public void saveCache(String key,Object value){
        this.saveCache(key,value,-1L);
    }

    public void saveCache(String key,Object value,long duration){
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        String valueStr = mGson.toJson(value);
        if (duration!=-1L){
            duration+=System.currentTimeMillis();
        }
        CacheWithDuration cacheWithDuration=new CacheWithDuration(duration,valueStr);
        String cacheWithTime = mGson.toJson(cacheWithDuration);
        edit.putString(key,cacheWithTime);
        edit.apply();
    }

    public void deleteCache(String key){
        mSharedPreferences.edit().remove(key).apply();
    }
    public <T> T getValue(String key, Class<T> clazz){
        String valueWithDuration = mSharedPreferences.getString(key,null);

        if (valueWithDuration==null){
            return null;
        }
        CacheWithDuration cacheWithDuration = mGson.fromJson(valueWithDuration, CacheWithDuration.class);
        long duration = cacheWithDuration.getDuration();
        if (duration!=-1&&duration - System.currentTimeMillis()<=0){
           //过期
            return null;
        }else {
            //未过期
            String cache = cacheWithDuration.getCache();
            T result = mGson.fromJson(cache, clazz);
            return result;
        }
    }

    private static JsonCacheUtils sJsonCacheUtils=null;

    public static JsonCacheUtils getInstance(){
        if (sJsonCacheUtils == null) {
            sJsonCacheUtils=new JsonCacheUtils();
        }
        return sJsonCacheUtils;
    }
}
