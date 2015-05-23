package com.example.team.myapplication.Cache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by coco on 2015/4/24.
 */
public class LruCacheImageLoader {
    private static LruCacheImageLoader mLruCacheImageLoader;
    private static LruCache<String, Bitmap> mLruCache;
    private LruCacheImageLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int size = maxMemory / 6;
        //设定LruCache的缓存为可用内存的六分之一
        mLruCache = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }
    public static LruCacheImageLoader getLruCacheImageLoaderInstance(){
        if (mLruCacheImageLoader==null) {
            mLruCacheImageLoader=new LruCacheImageLoader();
        }
        return mLruCacheImageLoader;
    }
    /**
     * 从LruCache中获取图片,若不存在返回null
     */
    public static Bitmap getBitmapFromLruCache(String key){
        return mLruCache.get(key);
    }
    /**
         * 往LruCache中添加图片.
         * 当然要首先判断LruCache中是否已经存在该图片,若不存在再添加
         */
    public static void addBitmapToLruCache(String key,Bitmap bitmap){
        if (getBitmapFromLruCache(key)==null) {
            mLruCache.put(key, bitmap);
        }
    }
}
