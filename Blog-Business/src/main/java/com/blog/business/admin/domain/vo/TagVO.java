package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yujunhong
 * @date 2021/9/23 11:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagVO {
    /**
     * 唯一UID
     */
    private String uid;

    /**
     * 标签内容
     */
    private String content;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

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
