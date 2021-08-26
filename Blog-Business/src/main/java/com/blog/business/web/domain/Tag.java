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
 * 标签表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:08
 */
@ApiModel(value = "标签表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_tag")
public class Tag {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 标签内容
     */
    @ApiModelProperty(value = "标签内容")
    @Excel(name = "标签内容")
    private String content;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Integer status;
    /**
     * 标签简介
     */
    @ApiModelProperty(value = "标签简介")
    @Excel(name = "标签简介")
    private Integer clickCount;
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
     * 排序字段，越大越靠前
     */
    @ApiModelProperty(value = "排序字段，越大越靠前")
    @Excel(name = "排序字段，越大越靠前")
    private Integer sort;
}
