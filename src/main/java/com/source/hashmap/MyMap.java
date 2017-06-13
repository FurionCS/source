package com.source.hashmap;

/**
 * @Description 推崇面向接口编程的思想
 * @Author ErnestCheng
 * @Date 2017/6/13.
 */
public interface MyMap<K,V> {
    /**
     * 存储
     * @param k
     * @param v
     * @return
     */
    public V put(K k,V v);

    /**
     * 获得
     * @param k
     * @return
     */
    public V get(K k);
    //内部接口
    public interface Entry<K,V>{
        public K getKey();
        public V getValue();
    }

}
