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
 * 存储信息表
 *
 * @author yujunhong
 * @date 2021/06/03 11:58:14
 */
@ApiModel(value = "存储信息表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_storage")
public class Storage {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 管理员uid
     */
    @ApiModelProperty(value = "管理员uid")
    @Excel(name = "管理员uid")
    private String adminUid;
    /**
     * 网盘容量大小
     */
    @ApiModelProperty(value = "网盘容量大小")
    @Excel(name = "网盘容量大小")
    private Long storageSize;
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
     * 最大容量大小
     */
    @ApiModelProperty(value = "最大容量大小")
    @Excel(name = "最大容量大小")
    private Long maxStorageSize;
}
