package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门户页导航栏VO
 *
 * @author yujunhong
 * @date 2021/10/11 15:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebNavbarVO {
    /**
     * 唯一UID
     */
    private String uid;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 导航栏级别 （一级分类，二级分类）
     */
    private String navbarLevel;

    /**
     * 介绍
     */
    private String summary;

    /**
     * Icon图标
     */
    private String icon;

    /**
     * 父UID
     */
    private String parentUid;

    /**
     * URL地址
     */
    private String url;

    /**
     * 排序字段(越大越靠前)
     */
    private Integer sort;

    /**
     * 是否显示  1: 是  0: 否
     */
    private Integer isShow;

    /**
     * 是否跳转外部URL，如果是，那么路由为外部的链接
     */
    private Integer isJumpExternalUrl;

    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;

    /**
     * 关键字
     */
    private String keyword;
}
