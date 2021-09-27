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
 * @author yujunhong
 * @date 2021/06/01 11:09:10
 */
@ApiModel(value = "")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_web_config")
public class WebConfig {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @Excel(name = "主键")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * logo(文件UID)
     */
    @ApiModelProperty(value = "logo(文件UID)")
    @Excel(name = "logo(文件UID)")
    private String logo;
    /**
     * 网站名称
     */
    @ApiModelProperty(value = "网站名称")
    @Excel(name = "网站名称")
    private String name;
    /**
     * 介绍
     */
    @ApiModelProperty(value = "介绍")
    @Excel(name = "介绍")
    private String summary;
    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    @Excel(name = "关键字")
    private String keyword;
    /**
     * 作者
     */
    @ApiModelProperty(value = "作者")
    @Excel(name = "作者")
    private String author;
    /**
     * 备案号
     */
    @ApiModelProperty(value = "备案号")
    @Excel(name = "备案号")
    private String recordNum;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private Integer status;
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
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @Excel(name = "标题")
    private String title;
    /**
     * 支付宝收款码FileId
     */
    @ApiModelProperty(value = "支付宝收款码FileId")
    @Excel(name = "支付宝收款码FileId")
    private String aliPay;
    /**
     * 微信收款码FileId
     */
    @ApiModelProperty(value = "微信收款码FileId")
    @Excel(name = "微信收款码FileId")
    private String weixinPay;
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
     * QQ号
     */
    @ApiModelProperty(value = "QQ号")
    @Excel(name = "QQ号")
    private String qqNumber;
    /**
     * QQ群
     */
    @ApiModelProperty(value = "QQ群")
    @Excel(name = "QQ群")
    private String qqGroup;
    /**
     * 微信号
     */
    @ApiModelProperty(value = "微信号")
    @Excel(name = "微信号")
    private String weChat;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;
    /**
     * 显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）
     */
    @ApiModelProperty(value = "显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）")
    @Excel(name = "显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）")
    private String showList;
    /**
     * 登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）
     */
    @ApiModelProperty(value = "登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）")
    @Excel(name = "登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）")
    private String loginTypeList;
    /**
     * 是否开启评论(0:否 1:是)
     */
    @ApiModelProperty(value = "是否开启评论(0:否 1:是)")
    @Excel(name = "是否开启评论(0:否 1:是)")
    private String openComment;
    /**
     * 是否开启移动端评论(0:否， 1:是)
     */
    @ApiModelProperty(value = "是否开启移动端评论(0:否， 1:是)")
    @Excel(name = "是否开启移动端评论(0:否， 1:是)")
    private String openMobileComment;
    /**
     * 是否开启赞赏(0:否， 1:是)
     */
    @ApiModelProperty(value = "是否开启赞赏(0:否， 1:是)")
    @Excel(name = "是否开启赞赏(0:否， 1:是)")
    private String openAdmiration;
    /**
     * 是否开启移动端赞赏(0:否， 1:是)
     */
    @ApiModelProperty(value = "是否开启移动端赞赏(0:否， 1:是)")
    @Excel(name = "是否开启移动端赞赏(0:否， 1:是)")
    private String openMobileAdmiration;
    /**
     * 友链申请模板, 添加友链申请模板格式
     */
    @ApiModelProperty(value = "友链申请模板, 添加友链申请模板格式")
    @Excel(name = "友链申请模板, 添加友链申请模板格式")
    private String linkApplyTemplate;

    /**
     * Logo图片
     */
    @TableField(exist = false)
    private String logoPhoto;


    /**
     * 支付宝付款码
     */
    @TableField(exist = false)
    private String aliPayPhoto;

    /**
     * 微信付款码
     */
    @TableField(exist = false)
    private String weixinPayPhoto;

    /**
     * 标题图
     */
    @TableField(exist = false)
    private List<String> photoList;
}
