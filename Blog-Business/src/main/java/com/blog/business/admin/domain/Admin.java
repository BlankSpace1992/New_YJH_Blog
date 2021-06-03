package com.blog.business.admin.domain;

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
 * @date 2021/06/01 11:07:42
 */
@ApiModel(value = "管理员表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_admin")
public class Admin {
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
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @Excel(name = "密码")
    private String passWord;
    /**
     * 性别(1:男2:女)
     */
    @ApiModelProperty(value = "性别(1:男2:女)")
    @Excel(name = "性别(1:男2:女)")
    private String gender;
    /**
     * 个人头像
     */
    @ApiModelProperty(value = "个人头像")
    @Excel(name = "个人头像")
    private String avatar;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;
    /**
     * 出生年月日
     */
    @ApiModelProperty(value = "出生年月日")
    @Excel(name = "出生年月日")
    private Object birthday;
    /**
     * 手机
     */
    @ApiModelProperty(value = "手机")
    @Excel(name = "手机")
    private String mobile;
    /**
     * 邮箱验证码
     */
    @ApiModelProperty(value = "邮箱验证码")
    @Excel(name = "邮箱验证码")
    private String validCode;
    /**
     * 自我简介最多150字
     */
    @ApiModelProperty(value = "自我简介最多150字")
    @Excel(name = "自我简介最多150字")
    private String summary;
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
    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @Excel(name = "昵称")
    private String nickName;
    /**
     * QQ号
     */
    @ApiModelProperty(value = "QQ号")
    @Excel(name = "QQ号")
    private String qqNumber;
    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号")
    @Excel(name = "微信号")
    private String weChat;
    /**
     * 职业
     */
    @ApiModelProperty(value = "职业")
    @Excel(name = "职业")
    private String occupation;
    /**
     * github地址
     */
    @ApiModelProperty(value = "github地址")
    @Excel(name = "github地址")
    private String github;
    /**
     * gitee地址
     */
    @ApiModelProperty(value = "gitee地址")
    @Excel(name = "gitee地址")
    private String gitee;
    /**
     * 拥有的角色uid
     */
    @ApiModelProperty(value = "拥有的角色uid")
    @Excel(name = "拥有的角色uid")
    private String roleUid;
    /**
     * 履历
     */
    @ApiModelProperty(value = "履历")
    @Excel(name = "履历")
    private String personResume;
}
