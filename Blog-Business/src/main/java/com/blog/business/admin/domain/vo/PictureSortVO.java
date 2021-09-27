package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 相册分类实体类
 *
 * @author yujunhong
 * @date 2021/9/24 11:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureSortVO {
    /**
     * 唯一UID
     */
    private String uid;

    /**
     * 父UID
     */
    private String parentUid;

    /**
     * 分类名
     */
    private String name;

    /**
     * 分类图片Uid
     */
    private String fileUid;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 是否显示  1: 是  0: 否
     */
    private Integer isShow;

    /**
     * 关键字
     */
    private String keyword;
    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;
}
