package com.source.hashmap;


import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ErnestCheng
 * @Date 2017/6/13.
 */
public class MyHashMap<K,V> implements  MyMap<K,V>{
    //定义默认数组大小 2^n
    private static int defaultLength=16;
    //扩容标准，所使用的userSize/数组长度 >0.75
    //defaultAddSizeFactor 过大 造成扩容概率变低，存储小但是就是存和取效率降低
    //比如使用0.9 有限的数组长度空间会形成链表，在存和取是需要大量的遍历列表
    private static double defaultAddSizeFactor=0.75;
    //使用数组的位置
    private int userSize;
    //定义Map骨架
    private Entry<K,V>[] table=null;
    //Spring 的门面模式
    public MyHashMap(){
        this(defaultLength,defaultAddSizeFactor);
    }
    public MyHashMap(int length,double defaultAddSizeFactor){
        if(length<0){
            throw new  IllegalArgumentException("参数不能为负数:"+length);
        }
        if(defaultAddSizeFactor<=0||Double.isNaN(defaultAddSizeFactor)){
            throw new IllegalArgumentException("扩容因子必须为大于0的数字"+defaultAddSizeFactor);
        }
        this.defaultLength=length;
        this.defaultAddSizeFactor=defaultAddSizeFactor;
        table=new Entry[defaultLength];
    }
    //快速存储
    public V put(K k, V v) {
        //判断是否扩容
        if(userSize>defaultAddSizeFactor*defaultLength){
            //进行两倍扩容
            up2Size();
        }
        //获得要插入的位置
        int index =getIndex(k,table.length);
        //判断要插入的位置是否有数据
        Entry<K,V> entry=table[index];
        //没有，直接插入
        if(entry==null){
            table[index]=new Entry(k,v,null);
            //使用位置+1
            userSize++;
        }else if(entry!=null){//有，插入链表
            table[index]=new Entry(k,v,entry);
        }
        return table[index].getValue();
    }

    public int getUserSize(){
        return userSize;
    }

    /**
     * 扩容
     */
    private void up2Size(){
        //先新建一个2倍的数组
        Entry<K,V>[] newTable=new Entry[2*defaultLength];
        //将老数组存的内容取出来
        againHash(newTable);
    }
    private void againHash(MyHashMap<K,V>.Entry<K,V>[] newTable){
        List<Entry<K,V>> entryList=new ArrayList<MyHashMap<K,V>.Entry<K,V>>();
        for(int i=0;i<table.length;i++){
            if(table[i]==null){
                continue;
            }
            //将数组中的对象放到entryList中
            foundEntryByNext(table[i],entryList);
        }
        if(entryList.size()>0){
            userSize=0;
            defaultLength=2*defaultLength;
            table=newTable;
            for(Entry<K,V> entry:entryList){
                //取消链表结构
                if(entry.next!=null){
                    entry.next=null;
                }
                //重新散列,调用put方法
                put(entry.getKey(),entry.getValue());
            }
        }

    }
    //重点是我们怎么去找存
    private void foundEntryByNext(Entry<K,V> entry,List<Entry<K,V>> entryList){
        if(entry!=null&&entry.next!=null){
            entryList.add(entry);
            //递归
            foundEntryByNext(entry.next,entryList);
        }else if(entry!=null&&entry.next==null){
            entryList.add(entry);
        }
    }


    /**
     * 通过k和长度来获得位置
     * @param k
     * @param length
     * @return
     */
    private int getIndex(K k ,int length){
        //length 2^n 0000 1111
        int m=length-1;
        //调用hash算法 index始终在[0,length)
        int index=hash(k.hashCode())&m;
        return index;
    }

    /**
     * hash算法
     * @param hashCode
     * @return
     */
    private int hash(int hashCode){
        //为什么是20,12，这个是有jdk大量优化后的结果
        hashCode=hashCode^(hashCode>>>20)^(hashCode>>>12);
        return hashCode^((hashCode>>>7)^(hashCode>>>4));
    }

    public V get(K k) {
        int index=getIndex(k,table.length);
        if(table[index]==null){
            throw new NullPointerException();
        }
        //key 存在情况
        return findValueByEqualKey(k,table[index]);
    }

    private V findValueByEqualKey(K k,MyHashMap<K,V>.Entry<K,V> entry){
        if(k==entry.getKey()||k.equals(entry.getKey())){
            return entry.getValue();
        }else if(entry.next!=null){
            //递归链表
           return findValueByEqualKey(k,entry.next);
        }
        return null;
    }
    class Entry<K,V> implements  MyMap.Entry<K,V>{
        K k;
        V v;
        //指向被this挤压下去的Entry对象
        Entry<K,V> next;

        public Entry(K k,V v,Entry<K,V> next){
            this.k=k;
            this.v=v;
            this.next=next;
        }
        public K getKey() {
            return k;
        }
        public V getValue() {
            return v;
        }
    }
}
