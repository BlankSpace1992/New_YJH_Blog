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
 * 图片表
 *
 * @author yujunhong
 * @date 2021/06/01 11:09:02
 */
@ApiModel(value = "图片表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_picture")
public class Picture {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 图片uid
     */
    @ApiModelProperty(value = "图片uid")
    @Excel(name = "图片uid")
    private String fileUid;
    /**
     * 图片名
     */
    @ApiModelProperty(value = "图片名")
    @Excel(name = "图片名")
    private String picName;
    /**
     * 分类uid
     */
    @ApiModelProperty(value = "分类uid")
    @Excel(name = "分类uid")
    private String pictureSortUid;
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
