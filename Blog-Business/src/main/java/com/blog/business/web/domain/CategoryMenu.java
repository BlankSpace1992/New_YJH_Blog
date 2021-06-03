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
 * 管理员表
 *
 * @author yujunhong
 * @date 2021/06/01 11:08:59
 */
@ApiModel(value = "管理员表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_category_menu")
public class CategoryMenu {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    @Excel(name = "菜单名称")
    private String name;
    /**
     * 菜单级别
     */
    @ApiModelProperty(value = "菜单级别")
    @Excel(name = "菜单级别")
    private Object menuLevel;
    /**
     * 简介
     */
    @ApiModelProperty(value = "简介")
    @Excel(name = "简介")
    private String summary;
    /**
     * 父uid
     */
    @ApiModelProperty(value = "父uid")
    @Excel(name = "父uid")
    private String parentUid;
    /**
     * url地址
     */
    @ApiModelProperty(value = "url地址")
    @Excel(name = "url地址")
    private String url;
    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Excel(name = "图标")
    private String icon;
    /**
     * 排序字段，越大越靠前
     */
    @ApiModelProperty(value = "排序字段，越大越靠前")
    @Excel(name = "排序字段，越大越靠前")
    private Integer sort;
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
     * 是否显示 1:是 0:否
     */
    @ApiModelProperty(value = "是否显示 1:是 0:否")
    @Excel(name = "是否显示 1:是 0:否")
    private Object isShow;
    /**
     * 菜单类型 0: 菜单   1: 按钮
     */
    @ApiModelProperty(value = "菜单类型 0: 菜单   1: 按钮")
    @Excel(name = "菜单类型 0: 菜单   1: 按钮")
    private Object menuType;
    /**
     * 是否跳转外部链接 0：否，1：是
     */
    @ApiModelProperty(value = "是否跳转外部链接 0：否，1：是")
    @Excel(name = "是否跳转外部链接 0：否，1：是")
    private Object isJumpExternalUrl;
}
