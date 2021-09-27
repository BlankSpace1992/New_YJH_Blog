package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yujunhong
 * @date 2021/9/27 14:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkVO {
    /**
     * 唯一UID
     */
    private String uid;

    /**
     * 友链标题
     */
    private String title;
    /**
     * 友链介绍
     */
    private String summary;
    /**
     * 友链地址
     */
    private String url;

    /**
     * 友链状态： 0 申请中， 1：已上线，  2：已拒绝
     */
    private Integer linkStatus;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 站长邮箱
     */
    private String email;

    /**
     * 网站图标uid
     */
    private String fileUid;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

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
