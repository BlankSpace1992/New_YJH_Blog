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
 * 博客爬取表
 *
 * @author yujunhong
 * @date 2021/06/01 11:08:57
 */
@ApiModel(value = "博客爬取表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_blog_spider")
public class BlogSpider {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
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
    private Object content;
    /**
     * 标签uid
     */
    @ApiModelProperty(value = "标签uid")
    @Excel(name = "标签uid")
    private String tagUid;
    /**
     * 博客点击数
     */
    @ApiModelProperty(value = "博客点击数")
    @Excel(name = "博客点击数")
    private Integer clickCount;
    /**
     * 博客收藏数
     */
    @ApiModelProperty(value = "博客收藏数")
    @Excel(name = "博客收藏数")
    private Integer collectCount;
    /**
     * 标题图片uid
     */
    @ApiModelProperty(value = "标题图片uid")
    @Excel(name = "标题图片uid")
    private String fileUid;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Object status;
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
     * 管理员uid
     */
    @ApiModelProperty(value = "管理员uid")
    @Excel(name = "管理员uid")
    private String adminUid;
    /**
     * 是否原创（0:不是 1：是）
     */
    @ApiModelProperty(value = "是否原创（0:不是 1：是）")
    @Excel(name = "是否原创（0:不是 1：是）")
    private String isOriginal;
    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    @Excel(name = "作者")
    private String author;
    /**
     * 文章出处
     */
    @ApiModelProperty(value = "文章出处")
    @Excel(name = "文章出处")
    private String articlesPart;
    /**
     * 博客分类UID
     */
    @ApiModelProperty(value = "博客分类UID")
    @Excel(name = "博客分类UID")
    private String blogSortUid;
    /**
     * 推荐等级(0:正常)
     */
    @ApiModelProperty(value = "推荐等级(0:正常)")
    @Excel(name = "推荐等级(0:正常)")
    private Object level;
    /**
     * 是否发布：0：否，1：是
     */
    @ApiModelProperty(value = "是否发布：0：否，1：是")
    @Excel(name = "是否发布：0：否，1：是")
    private String isPublish;
    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    @Excel(name = "排序字段")
    private Integer sort;
}
