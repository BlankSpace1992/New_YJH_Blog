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
 * 用户表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:09
 */
@ApiModel(value = "用户表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_user")
public class User {
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
    private Object gender;
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
     * 资料来源
     */
    @ApiModelProperty(value = "资料来源")
    @Excel(name = "资料来源")
    private String source;
    /**
     * 平台uuid
     */
    @ApiModelProperty(value = "平台uuid")
    @Excel(name = "平台uuid")
    private String uuid;
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
     * 评论状态 1:正常 0:禁言
     */
    @ApiModelProperty(value = "评论状态 1:正常 0:禁言")
    @Excel(name = "评论状态 1:正常 0:禁言")
    private Object commentStatus;
    /**
     * ip来源
     */
    @ApiModelProperty(value = "ip来源")
    @Excel(name = "ip来源")
    private String ipSource;
    /**
     * 浏览器
     */
    @ApiModelProperty(value = "浏览器")
    @Excel(name = "浏览器")
    private String browser;
    /**
     * 操作系统
     */
    @ApiModelProperty(value = "操作系统")
    @Excel(name = "操作系统")
    private String os;
    /**
     * 是否开启邮件通知 1:开启 0:关闭
     */
    @ApiModelProperty(value = "是否开启邮件通知 1:开启 0:关闭")
    @Excel(name = "是否开启邮件通知 1:开启 0:关闭")
    private Object startEmailNotification;
    /**
     * 用户标签：0：普通用户，1：管理员，2：博主 等
     */
    @ApiModelProperty(value = "用户标签：0：普通用户，1：管理员，2：博主 等")
    @Excel(name = "用户标签：0：普通用户，1：管理员，2：博主 等")
    private Object userTag;
}
