package com.blog.business.search.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * @author yujunhong
 * @date 2021/8/31 10:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ElasticSearchVO {
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @Id
    private String id;
    /**
     * 主键uid
     */
    @ApiModelProperty(value = "主键uid")
    @Excel(name = "主键uid")
    private String uid;
    /**
     * oid
     */
    @ApiModelProperty(value = "oid")
    @Excel(name = "oid")
    private Integer oid;
    /**
     * 类型【0 博客， 1：推广】
     */
    @ApiModelProperty(value = "类型【0 博客， 1：推广】")
    @Excel(name = "类型【0 博客， 1：推广】")
    private String type;
    /**
     * 博客标题
     */
    @ApiModelProperty(value = "博客标题")
    @Excel(name = "博客标题")
    private String title;
    /**
     * 博客简介
     */
    @ApiModelProperty(value = "博客简介")
    @Excel(name = "博客简介")
    private String summary;
    /**
     * 博客内容
     */
    @ApiModelProperty(value = "博客内容")
    @Excel(name = "博客内容")
    private String content;
    /**
     * 博客分类名称
     */
    @ApiModelProperty(value = "博客分类名称")
    @Excel(name = "博客分类名称")
    private String blogSortName;
    /**
     * 博客分类UID
     */
    @ApiModelProperty(value = "博客分类UID")
    @Excel(name = "博客分类UID")
    private String blogSortUid;
    /**
     * 是否发布：0：否，1：是
     */
    @ApiModelProperty(value = "是否发布：0：否，1：是")
    @Excel(name = "是否发布：0：否，1：是")
    private String isPublish;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;
    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    @Excel(name = "作者")
    private String author;
    /**
     * 博客标题图
     */
    @ApiModelProperty(value = "博客标题图")
    private String photoUrl;
    /**
     * 标签,一篇博客对应多个标签
     */
    @ApiModelProperty(value = "标签,一篇博客对应多个标签")
    private List<String> tagUidList;
    /**
     * 标签,一篇博客对应多个标签
     */
    @ApiModelProperty(value = "标签,一篇博客对应多个标签")
    private List<String> tagNameList;
}
