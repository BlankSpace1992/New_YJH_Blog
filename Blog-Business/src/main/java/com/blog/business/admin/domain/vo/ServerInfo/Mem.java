package com.blog.business.admin.domain.vo.ServerInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 內存相关信息
 *
 * @author yujunhong
 * @date 2021/10/11 16:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mem {
    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

    /**
     * 使用率
     */
    private double usage;
}
