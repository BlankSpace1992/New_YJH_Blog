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
 * 参数配置表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:08
 */
@ApiModel(value = "参数配置表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_sys_params")
public class SysParams {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @Excel(name = "主键")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 配置类型 是否系统内置(1:，是 0:否)
     */
    @ApiModelProperty(value = "配置类型 是否系统内置(1:，是 0:否)")
    @Excel(name = "配置类型 是否系统内置(1:，是 0:否)")
    private String paramsType;
    /**
     * 参数名称
     */
    @ApiModelProperty(value = "参数名称")
    @Excel(name = "参数名称")
    private String paramsName;
    /**
     * 参数键名
     */
    @ApiModelProperty(value = "参数键名")
    @Excel(name = "参数键名")
    private String paramsKey;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;
    /**
     * 参数键值
     */
    @ApiModelProperty(value = "参数键值")
    @Excel(name = "参数键值")
    private String paramsValue;
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
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    @Excel(name = "排序字段")
    private Integer sort;
}
