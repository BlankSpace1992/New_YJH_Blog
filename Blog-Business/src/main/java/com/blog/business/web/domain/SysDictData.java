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
 * 字典数据表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:06
 */
@ApiModel(value = "字典数据表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_sys_dict_data")
public class SysDictData {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @Excel(name = "主键")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 自增键oid
     */
    @ApiModelProperty(value = "自增键oid")
    @Excel(name = "自增键oid")
    private Integer oid;
    /**
     * 字典类型UID
     */
    @ApiModelProperty(value = "字典类型UID")
    @Excel(name = "字典类型UID")
    private String dictTypeUid;
    /**
     * 字典标签
     */
    @ApiModelProperty(value = "字典标签")
    @Excel(name = "字典标签")
    private String dictLabel;
    /**
     * 字典键值
     */
    @ApiModelProperty(value = "字典键值")
    @Excel(name = "字典键值")
    private String dictValue;
    /**
     * 样式属性（其他样式扩展）
     */
    @ApiModelProperty(value = "样式属性（其他样式扩展）")
    @Excel(name = "样式属性（其他样式扩展）")
    private String cssClass;
    /**
     * 表格回显样式
     */
    @ApiModelProperty(value = "表格回显样式")
    @Excel(name = "表格回显样式")
    private String listClass;
    /**
     * 是否默认（1是 0否）,默认为0
     */
    @ApiModelProperty(value = "是否默认（1是 0否）,默认为0")
    @Excel(name = "是否默认（1是 0否）,默认为0")
    private Integer isDefault;
    /**
     * 创建人UID
     */
    @ApiModelProperty(value = "创建人UID")
    @Excel(name = "创建人UID")
    private String createByUid;
    /**
     * 最后更新人UID
     */
    @ApiModelProperty(value = "最后更新人UID")
    @Excel(name = "最后更新人UID")
    private String updateByUid;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;
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
     * 是否发布(1:是，0:否)
     */
    @ApiModelProperty(value = "是否发布(1:是，0:否)")
    @Excel(name = "是否发布(1:是，0:否)")
    private String isPublish;
    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    @Excel(name = "排序字段")
    private Integer sort;
}
