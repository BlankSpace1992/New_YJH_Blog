package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * SysDictTypeVO
 * </p>
 *
 * @author 陌溪
 * @since 2020年2月15日21:20:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysDictTypeVO {
    /**
     * 唯一UID
     */
    private String uid;

    /**
     * 自增键 oid
     */
    private Long oid;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 是否发布  1：是，0:否，默认为0
     */
    private String isPublish;

    /**
     * 备注
     */
    private String remark;

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
