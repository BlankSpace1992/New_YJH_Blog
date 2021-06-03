package com.blog.business.picture.domain;

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
 * 文件分类表
 *
 * @author yujunhong
 * @date 2021/06/03 11:58:14
 */
@ApiModel(value = "文件分类表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_file_sort")
public class FileSort {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 项目名
     */
    @ApiModelProperty(value = "项目名")
    @Excel(name = "项目名")
    private String projectName;
    /**
     * 分类名
     */
    @ApiModelProperty(value = "分类名")
    @Excel(name = "分类名")
    private String sortName;
    /**
     * 分类路径
     */
    @ApiModelProperty(value = "分类路径")
    @Excel(name = "分类路径")
    private String url;
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
