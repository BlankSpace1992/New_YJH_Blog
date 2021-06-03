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
 * 系统配置表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:08
 */
@ApiModel(value = "系统配置表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_system_config")
public class SystemConfig {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @Excel(name = "主键")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 七牛云公钥
     */
    @ApiModelProperty(value = "七牛云公钥")
    @Excel(name = "七牛云公钥")
    private String qiNiuAccessKey;
    /**
     * 七牛云私钥
     */
    @ApiModelProperty(value = "七牛云私钥")
    @Excel(name = "七牛云私钥")
    private String qiNiuSecretKey;
    /**
     * 邮箱账号
     */
    @ApiModelProperty(value = "邮箱账号")
    @Excel(name = "邮箱账号")
    private String email;
    /**
     * 邮箱发件人用户名
     */
    @ApiModelProperty(value = "邮箱发件人用户名")
    @Excel(name = "邮箱发件人用户名")
    private String emailUserName;
    /**
     * 邮箱密码
     */
    @ApiModelProperty(value = "邮箱密码")
    @Excel(name = "邮箱密码")
    private String emailPassword;
    /**
     * SMTP地址
     */
    @ApiModelProperty(value = "SMTP地址")
    @Excel(name = "SMTP地址")
    private String smtpAddress;
    /**
     * SMTP端口
     */
    @ApiModelProperty(value = "SMTP端口")
    @Excel(name = "SMTP端口")
    private String smtpPort;
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
     * 七牛云上传空间
     */
    @ApiModelProperty(value = "七牛云上传空间")
    @Excel(name = "七牛云上传空间")
    private String qiNiuBucket;
    /**
     * 七牛云存储区域 华东（z0），华北(z1)，华南(z2)，北美(na0)，东南亚(as0)
     */
    @ApiModelProperty(value = "七牛云存储区域 华东（z0），华北(z1)，华南(z2)，北美(na0)，东南亚(as0)")
    @Excel(name = "七牛云存储区域 华东（z0），华北(z1)，华南(z2)，北美(na0)，东南亚(as0)")
    private String qiNiuArea;
    /**
     * 图片是否上传七牛云 (0:否， 1：是)
     */
    @ApiModelProperty(value = "图片是否上传七牛云 (0:否， 1：是)")
    @Excel(name = "图片是否上传七牛云 (0:否， 1：是)")
    private String uploadQiNiu;
    /**
     * 图片是否上传本地存储 (0:否， 1：是)
     */
    @ApiModelProperty(value = "图片是否上传本地存储 (0:否， 1：是)")
    @Excel(name = "图片是否上传本地存储 (0:否， 1：是)")
    private String uploadLocal;
    /**
     * 图片显示优先级（ 1 展示 七牛云,  0 本地）
     */
    @ApiModelProperty(value = "图片显示优先级（ 1 展示 七牛云,  0 本地）")
    @Excel(name = "图片显示优先级（ 1 展示 七牛云,  0 本地）")
    private String picturePriority;
    /**
     * 七牛云域名前缀：http://images.moguit.cn
     */
    @ApiModelProperty(value = "七牛云域名前缀：http://images.moguit.cn")
    @Excel(name = "七牛云域名前缀：http://images.moguit.cn")
    private String qiNiuPictureBaseUrl;
    /**
     * 本地服务器域名前缀：http://localhost:8600
     */
    @ApiModelProperty(value = "本地服务器域名前缀：http://localhost:8600")
    @Excel(name = "本地服务器域名前缀：http://localhost:8600")
    private String localPictureBaseUrl;
    /**
     * 是否开启邮件通知(0:否， 1:是)
     */
    @ApiModelProperty(value = "是否开启邮件通知(0:否， 1:是)")
    @Excel(name = "是否开启邮件通知(0:否， 1:是)")
    private String startEmailNotification;
    /**
     * 编辑器模式，(0：富文本编辑器CKEditor，1：markdown编辑器Veditor)
     */
    @ApiModelProperty(value = "编辑器模式，(0：富文本编辑器CKEditor，1：markdown编辑器Veditor)")
    @Excel(name = "编辑器模式，(0：富文本编辑器CKEditor，1：markdown编辑器Veditor)")
    private Object editorModel;
    /**
     * 主题颜色
     */
    @ApiModelProperty(value = "主题颜色")
    @Excel(name = "主题颜色")
    private String themeColor;
    /**
     * Minio远程连接地址
     */
    @ApiModelProperty(value = "Minio远程连接地址")
    @Excel(name = "Minio远程连接地址")
    private String minioEndPoint;
    /**
     * Minio公钥
     */
    @ApiModelProperty(value = "Minio公钥")
    @Excel(name = "Minio公钥")
    private String minioAccessKey;
    /**
     * Minio私钥
     */
    @ApiModelProperty(value = "Minio私钥")
    @Excel(name = "Minio私钥")
    private String minioSecretKey;
    /**
     * Minio桶
     */
    @ApiModelProperty(value = "Minio桶")
    @Excel(name = "Minio桶")
    private String minioBucket;
    /**
     * 图片是否上传Minio (0:否， 1：是)
     */
    @ApiModelProperty(value = "图片是否上传Minio (0:否， 1：是)")
    @Excel(name = "图片是否上传Minio (0:否， 1：是)")
    private Object uploadMinio;
    /**
     * Minio服务器文件域名前缀
     */
    @ApiModelProperty(value = "Minio服务器文件域名前缀")
    @Excel(name = "Minio服务器文件域名前缀")
    private String minioPictureBaseUrl;
    /**
     * 是否开启仪表盘通知(0:否， 1:是)
     */
    @ApiModelProperty(value = "是否开启仪表盘通知(0:否， 1:是)")
    @Excel(name = "是否开启仪表盘通知(0:否， 1:是)")
    private Object openDashboardNotification;
    /**
     * 仪表盘通知【用于首次登录弹框】
     */
    @ApiModelProperty(value = "仪表盘通知【用于首次登录弹框】")
    @Excel(name = "仪表盘通知【用于首次登录弹框】")
    private Object dashboardNotification;
    /**
     * 博客详情图片显示优先级（ 0:本地  1: 七牛云 2: Minio）
     */
    @ApiModelProperty(value = "博客详情图片显示优先级（ 0:本地  1: 七牛云 2: Minio）")
    @Excel(name = "博客详情图片显示优先级（ 0:本地  1: 七牛云 2: Minio）")
    private Object contentPicturePriority;
    /**
     * 是否开启用户邮件激活功能【0 关闭，1 开启】
     */
    @ApiModelProperty(value = "是否开启用户邮件激活功能【0 关闭，1 开启】")
    @Excel(name = "是否开启用户邮件激活功能【0 关闭，1 开启】")
    private Object openEmailActivate;
}
