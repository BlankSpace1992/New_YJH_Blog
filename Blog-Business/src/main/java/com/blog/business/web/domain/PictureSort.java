package com.blog.business.web.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 图片分类表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:03
 */
@ApiModel(value = "图片分类表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_picture_sort")
public class PictureSort {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 分类图片uid
     */
    @ApiModelProperty(value = "分类图片uid")
    @Excel(name = "分类图片uid")
    private String fileUid;
    /**
     * 分类名
     */
    @ApiModelProperty(value = "分类名")
    @Excel(name = "分类名")
    private String name;
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
    /**
     * 排序字段，越大越靠前
     */
    @ApiModelProperty(value = "排序字段，越大越靠前")
    @Excel(name = "排序字段，越大越靠前")
    private Integer sort;
    /**
     * 是否显示，1：是，0，否
     */
    @ApiModelProperty(value = "是否显示，1：是，0，否")
    @Excel(name = "是否显示，1：是，0，否")
    private Integer isShow;

    /**
     * 分类图
     */
    @TableField(exist = false)
    private List<String> photoList;
}
