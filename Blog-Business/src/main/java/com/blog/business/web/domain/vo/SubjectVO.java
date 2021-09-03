package com.blog.business.web.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主题列表查询条件
 *
 * @author yujunhong
 * @date 2021/9/3 17:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectVO {
    /**
     * 专题名
     */
    @ApiModelProperty(value = "专题名")
    private String subjectName;

    /**
     * 专题介绍
     */
    @ApiModelProperty(value = "专题介绍")
    private String summary;

    /**
     * 封面图片UID
     */
    @ApiModelProperty(value = "封面图片UID")
    private String fileUid;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private Integer sort;

    /**
     * 专题点击数
     */
    @ApiModelProperty(value = "专题点击数")
    private String clickCount;

    /**
     * 专题收藏数
     */
    @ApiModelProperty(value = "专题收藏数")
    private String collectCount;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;

    /**
     * 当前页
     */ @ApiModelProperty(value = "当前页")
    private Long currentPage;

    /**
     * 页大小
     */ @ApiModelProperty(value = "页大小")
    private Long pageSize;
}
