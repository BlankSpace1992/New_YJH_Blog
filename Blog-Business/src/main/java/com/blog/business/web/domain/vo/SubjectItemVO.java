package com.blog.business.web.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yujunhong
 * @date 2021/9/3 17:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectItemVO {
    /**
     * 唯一UID
     */
    private String uid;
    /**
     * 专题UID
     */
    @ApiModelProperty(value = "专题UID")
    private String subjectUid;

    /**
     * 博客UID
     */
    @ApiModelProperty(value = "博客UID")
    private String blogUid;

    /**
     * 排序字段，数值越大，越靠前
     */
    @ApiModelProperty(value = "排序字段，数值越大，越靠前")
    private Integer sort;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页")
    private Long currentPage;

    /**
     * 页大小
     */
    @ApiModelProperty(value = "页大小")
    private Long pageSize;
}
