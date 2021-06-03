package com.blog.business.web.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @author yujunhong
 * @date 2021/06/01 11:20:38
 */
@ApiModel(value = "")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_web_navbar")
public class WebNavbar {
    /**
     * 标识id
     */
    @ApiModelProperty(value = "标识id")
    @Excel(name = "标识id")
    private String uid;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Excel(name = "名称")
    private String name;
    /**
     * 级别
     */
    @ApiModelProperty(value = "级别")
    @Excel(name = "级别")
    private String navbarLevel;
    /**
     * 概要
     */
    @ApiModelProperty(value = "概要")
    @Excel(name = "概要")
    private String summary;
    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    @Excel(name = "父级id")
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
     * 是否展示
     */
    @ApiModelProperty(value = "是否展示")
    @Excel(name = "是否展示")
    private Object isShow;
    /**
     * 是否跳转
     */
    @ApiModelProperty(value = "是否跳转")
    @Excel(name = "是否跳转")
    private Object isJumpExternalUrl;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @Excel(name = "排序")
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
     * 父菜单
     */
    @TableField(exist = false)
    private WebNavbar parentWebNavbar;

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<WebNavbar> childWebNavbar;
}
