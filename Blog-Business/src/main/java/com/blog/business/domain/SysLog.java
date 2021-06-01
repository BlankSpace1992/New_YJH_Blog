package com.blog.business.domain;

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
 * @author yujunhong
 * @date 2021/06/01 11:09:07
 */
@ApiModel(value = "系统日志")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_log")
public class SysLog {
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
     * 管理员uid
     */
    @ApiModelProperty(value = "管理员uid")
    @Excel(name = "管理员uid")
    private String adminUid;
    /**
     * 请求ip地址
     */
    @ApiModelProperty(value = "请求ip地址")
    @Excel(name = "请求ip地址")
    private String ip;
    /**
     * 请求url
     */
    @ApiModelProperty(value = "请求url")
    @Excel(name = "请求url")
    private String url;
    /**
     * 请求方式
     */
    @ApiModelProperty(value = "请求方式")
    @Excel(name = "请求方式")
    private String type;
    /**
     * 请求类路径
     */
    @ApiModelProperty(value = "请求类路径")
    @Excel(name = "请求类路径")
    private String classPath;
    /**
     * 请求方法名
     */
    @ApiModelProperty(value = "请求方法名")
    @Excel(name = "请求方法名")
    private String method;
    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数")
    @Excel(name = "请求参数")
    private Object params;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Excel(name = "描述")
    private String operation;
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
     * ip来源
     */
    @ApiModelProperty(value = "ip来源")
    @Excel(name = "ip来源")
    private String ipSource;
    /**
     * 方法请求花费的时间
     */
    @ApiModelProperty(value = "方法请求花费的时间")
    @Excel(name = "方法请求花费的时间")
    private Integer spendTime;
}
