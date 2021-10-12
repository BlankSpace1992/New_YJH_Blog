package com.blog.business.admin.domain.vo.ServerInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JVM相关信息
 *
 * @author yujunhong
 * @date 2021/10/11 16:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Jvm {
    /**
     * 虚拟机名称
     */
    private String name;

    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JVM可用内存(M)
     */
    private double used;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    /**
     * 使用率
     */
    private double usage;

    /**
     * JDK启动时间
     */
    private String startTime;

    /**
     * JDK运行时间
     */
    private String runTime;
}
