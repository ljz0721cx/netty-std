package com.janle.std.cases.pools.worker;

/**
 * 默认垃圾收集器为PS Scavenge+PS MarkSweep
 * 测试配置：-Xms30m -Xmx30m -Xmn10m -XX:+PrintGCDetails -XX:+PrintHeapAtGC
 * 默认分配：eden space:from space:to space。默认比例是8:1:1  可以适当设置比例调整gc次数
 * <p>
 * <p>
 * <p>
 * 总结
 * 1.eden区满时，触发MinorGC。即申请一个对象时，发现eden区不够用，则触发一次MinorGC。
 * Created by lijianzhen1 on 2019/1/21.
 */
public class DefaultJdk8GcTest {
    private static final int _1M = 1024 * 1024;


    public static void main(String[] args) {
        //testMinorGC();

        testFullGCOldGenLessEden();

        Obj _8m0 = new Obj(_1M * 1, "-8m");
    }

    /**
     * _05m _1m _2m _3m 创建4个对象后eden剩余245k，
     * PSYoungGen      total 9216K, used 8016K [0x00000007bf600000, 0x00000007c0000000, 0x00000007c0000000)
     * eden space 8192K, 97% used [0x00000007bf600000,0x00000007bfdd40c8,0x00000007bfe00000)
     * <p>
     * 创建_2m0时候需要2048K，eden space不够给新的对象分配内存，内存不够执行youngGC，看到下边eden从8016K回收到了1008K
     * [GC (Allocation Failure) [PSYoungGen: 8016K->1008K(9216K)] 8016K->5128K(29696K), 0.0041494 secs] [Times: user=0.01 sys=0.01, real=0.01 secs]
     */
    private static void testMinorGC() {
        /* eden space为8M，from/to space各为1M */
        Obj _05m = new Obj(_1M * 1 / 2, "_05m");
        Obj _1m = new Obj(_1M * 1, "-1m");
        Obj _2m = new Obj(_1M * 2, "-2m");
        _2m = null;
        Obj _3m = new Obj(_1M * 3, "-3m");
        //执行到此处使用 ed1
        Obj _2m0 = new Obj(_1M * 2, "-2m0");
    }


    /**
     * 老年代回收
     * 1.初始化申请超过最大的堆内存，会发生oom。
     * 2.如果没有超过eden大小，大于from和to的内存大小，并且老年代内存不足，执行fullgc。
     * 3.方法执行内申请的内存，只能在年轻代回收，方法栈退出后才能释放老年代数据，可以看到可达性分析有基于方法栈的。
     * 4.yuonggc可以回收方法内的申请的内存。
     */
    private static void testFullGCOldGenLessEden(){
        Obj _8m0 = new Obj(_1M * 8, "-8m");
        Obj _8m1 = new Obj(_1M * 8, "-8m");
        Obj _4m2 = new Obj(_1M * 4, "-4m");
        //_4m2=null;
        Obj _4m3 = new Obj(_1M * 3, "-3m");
        for (int i = 0; i < 10; i++) {
            Obj _1m0 = new Obj(_1M * 3, "-2m");
        }
        Obj _1m0 = new Obj(_1M * 3, "-3m");
    }
}

class Obj {
    private byte[] bytes;
    private String name;


    public Obj(int bytes, String name) {
        this.bytes = new byte[bytes];
        this.name = name;
        System.out.println("初始化对象:" + name);
    }


    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

