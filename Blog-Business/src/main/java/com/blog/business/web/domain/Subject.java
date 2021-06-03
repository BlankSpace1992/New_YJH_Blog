package com.blog.business.web.domain;

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
 * 专题表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:05
 */
@ApiModel(value = "专题表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_subject")
public class Subject {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @Excel(name = "主键")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 专题名称
     */
    @ApiModelProperty(value = "专题名称")
    @Excel(name = "专题名称")
    private String subjectName;
    /**
     * 简介
     */
    @ApiModelProperty(value = "简介")
    @Excel(name = "简介")
    private String summary;
    /**
     * 封面图片UID
     */
    @ApiModelProperty(value = "封面图片UID")
    @Excel(name = "封面图片UID")
    private String fileUid;
    /**
     * 专题点击数
     */
    @ApiModelProperty(value = "专题点击数")
    @Excel(name = "专题点击数")
    private Integer clickCount;
    /**
     * 专题收藏数
     */
    @ApiModelProperty(value = "专题收藏数")
    @Excel(name = "专题收藏数")
    private Integer collectCount;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Object status;
    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    @Excel(name = "排序字段")
    private Integer sort;
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
}
