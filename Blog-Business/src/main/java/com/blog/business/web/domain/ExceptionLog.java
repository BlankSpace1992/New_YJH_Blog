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
 * @author yujunhong
 * @date 2021/06/01 11:09:01
 */
@ApiModel(value = "")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_exception_log")
public class ExceptionLog {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 异常对象json格式
     */
    @ApiModelProperty(value = "异常对象json格式")
    @Excel(name = "异常对象json格式")
    private Object exceptionJson;
    /**
     * 异常信息
     */
    @ApiModelProperty(value = "异常信息")
    @Excel(name = "异常信息")
    private Object exceptionMessage;
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
     * ip地址
     */
    @ApiModelProperty(value = "ip地址")
    @Excel(name = "ip地址")
    private String ip;
    /**
     * ip来源
     */
    @ApiModelProperty(value = "ip来源")
    @Excel(name = "ip来源")
    private String ipSource;
    /**
     * 请求方法
     */
    @ApiModelProperty(value = "请求方法")
    @Excel(name = "请求方法")
    private String method;
    /**
     * 方法描述
     */
    @ApiModelProperty(value = "方法描述")
    @Excel(name = "方法描述")
    private String operation;
    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数")
    @Excel(name = "请求参数")
    private Object params;
}
