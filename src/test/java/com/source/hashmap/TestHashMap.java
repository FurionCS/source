package com.source.hashmap;



/**
 * @Description 测试hashMap
 * @Author ErnestCheng
 * @Date 2017/6/13.
 */
public class TestHashMap {

    public static void main(String[] args){
        MyMap<String,String> myMap=new MyHashMap<String,String>();
        for(int i=0;i<1000;i++){
            myMap.put("key"+i,"value"+i);
        }
        System.out.println("!---------------------------------------------------------!");
        for(int i=0;i<1000;i++){
            System.out.println("key:"+"key"+i+"  value:"+myMap.get("key"+i));
        }
    }

}
