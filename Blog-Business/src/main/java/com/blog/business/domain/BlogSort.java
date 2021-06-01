package com.blog.business.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 博客分类表
 *
 * @author yujunhong
 * @date 2021/06/01 11:08:49
 */
@ApiModel(value = "博客分类表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_blog_sort")
public class BlogSort {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 分类内容
     */
    @ApiModelProperty(value = "分类内容")
    @Excel(name = "分类内容")
    private String sortName;
    /**
     * 分类简介
     */
    @ApiModelProperty(value = "分类简介")
    @Excel(name = "分类简介")
    private String content;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Object status;
    /**
     * 排序字段，越大越靠前
     */
    @ApiModelProperty(value = "排序字段，越大越靠前")
    @Excel(name = "排序字段，越大越靠前")
    private Integer sort;
    /**
     * 点击数
     */
    @ApiModelProperty(value = "点击数")
    @Excel(name = "点击数")
    private Integer clickCount;
}
