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
 * 学习视频表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:04
 */
@ApiModel(value = "学习视频表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_study_video")
public class StudyVideo {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 视频封面图片uid
     */
    @ApiModelProperty(value = "视频封面图片uid")
    @Excel(name = "视频封面图片uid")
    private String fileUid;
    /**
     * 资源分类UID
     */
    @ApiModelProperty(value = "资源分类UID")
    @Excel(name = "资源分类UID")
    private String resourceSortUid;
    /**
     * 视频名称
     */
    @ApiModelProperty(value = "视频名称")
    @Excel(name = "视频名称")
    private String name;
    /**
     * 视频简介
     */
    @ApiModelProperty(value = "视频简介")
    @Excel(name = "视频简介")
    private String summary;
    /**
     * 分类介绍
     */
    @ApiModelProperty(value = "分类介绍")
    @Excel(name = "分类介绍")
    private String content;
    /**
     * 百度云完整路径
     */
    @ApiModelProperty(value = "百度云完整路径")
    @Excel(name = "百度云完整路径")
    private String baiduPath;
    /**
     * 点击数
     */
    @ApiModelProperty(value = "点击数")
    @Excel(name = "点击数")
    private String clickCount;
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
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    private String parentUid;
}
