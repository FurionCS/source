# source
源码阅读与手写

##hashMap
关键点：
 - defaultLength (默认数组大小2^n)
 - defaultAddSizeFactor (负载因子)
 - userSize (使用的数组大小)
 - Entry<K,V>[] (Map中的骨架)

**问题1：数组大小为什么是2的倍数**

    
     private int getIndex(K k ,int length){
        //length 2^n 0000 1111
        int m=length-1;
        //调用hash算法 index始终在[0,length)
        int index=hash(k.hashCode())&m;
        return index;
    }

  &nbsp;&nbsp;&nbsp;&nbsp;2^n转换成二进制就是1+n个0，减1之后就是0+n个1，如16 -> 10000，15 -> 01111，那根据&位运算的规则，都为1(真)时，才为1，那0≤运算后的结果≤15，假设hash(k.hashCode()) <= 15，那么运算后的结果就是hash(k.hashCode()) 本身，hash(k.hashCode()) >15，运算后的结果就是最后三位二进制做&运算后的值，最终，就是%运算后的余数，我想，这就是容量必须为2的幂的原因

**问题2：为什么负载因子为0.75**
   &nbsp;&nbsp;&nbsp;&nbsp;defaultAddSizeFactor 过大 造成扩容概率变低，存储小但是就是存和取效率降低
    &nbsp;&nbsp;&nbsp;&nbsp;比如使用0.9 有限的数组长度空间会形成链表，在存和取是需要大量的遍历列表

put方法实现
 - 判断是否扩容，更加userSize 是否大于负载因子*长度
 - 扩容，通过新建一个两倍的数组将老数组中的数据放入list中，链表中的数据通过递归获得，然后put入扩容后的数组，
 - 获得要插入数组的位置（根据hash算法，进行位操作进行获取）
 - 判断要插入的位置是否有数据
 - 没有，直接插入
 - 有，将链表向下压
 - 返回

get 方法实现

- 获得在数组中的位置
- 判断是否存在
- 存在，看第一个是不是相同key,相同则返回，不同则递归遍历链表