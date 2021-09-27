package com.blog.business.web.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 友情链接表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:02
 */
@ApiModel(value = "友情链接表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_link")
public class Link {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.ASSIGN_UUID)
    private String uid;
    /**
     * 友情链接标题
     */
    @ApiModelProperty(value = "友情链接标题")
    @Excel(name = "友情链接标题")
    private String title;
    /**
     * 友情链接介绍
     */
    @ApiModelProperty(value = "友情链接介绍")
    @Excel(name = "友情链接介绍")
    private String summary;
    /**
     * 友情链接URL
     */
    @ApiModelProperty(value = "友情链接URL")
    @Excel(name = "友情链接URL")
    private String url;
    /**
     * 点击数
     */
    @ApiModelProperty(value = "点击数")
    @Excel(name = "点击数")
    private Integer clickCount;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间")
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
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
     * 友链状态： 0 申请中， 1：已上线，  2：已下架
     */
    @ApiModelProperty(value = "友链状态： 0 申请中， 1：已上线，  2：已下架")
    @Excel(name = "友链状态： 0 申请中， 1：已上线，  2：已下架")
    private Integer linkStatus;
    /**
     * 申请用户UID
     */
    @ApiModelProperty(value = "申请用户UID")
    @Excel(name = "申请用户UID")
    private String userUid;
    /**
     * 操作管理员UID
     */
    @ApiModelProperty(value = "操作管理员UID")
    @Excel(name = "操作管理员UID")
    private String adminUid;
    /**
     * 站长邮箱
     */
    @ApiModelProperty(value = "站长邮箱")
    @Excel(name = "站长邮箱")
    private String email;
    /**
     * 网站图标
     */
    @ApiModelProperty(value = "网站图标")
    @Excel(name = "网站图标")
    private String fileUid;

    /**
     * 网站图标URL 【该字段不存入数据库】
     */
    @TableField(exist = false)
    private List<String> photoList;
}
