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
 * 游客表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:09
 */
@ApiModel(value = "游客表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_visitor")
public class Visitor {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @Excel(name = "用户名")
    private String userName;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;
    /**
     * 登录次数
     */
    @ApiModelProperty(value = "登录次数")
    @Excel(name = "登录次数")
    private Object loginCount;
    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间")
    @Excel(name = "最后登录时间")
    private Date lastLoginTime;
    /**
     * 最后登录IP
     */
    @ApiModelProperty(value = "最后登录IP")
    @Excel(name = "最后登录IP")
    private String lastLoginIp;
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
}
