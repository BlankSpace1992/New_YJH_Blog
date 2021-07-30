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
 * 文件表
 *
 * @author yujunhong
 * @date 2021/06/03 11:58:03
 */
@ApiModel(value = "文件表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_file")
public class File {
    /**
     * 唯一uid
     */
    @ApiModelProperty(value = "唯一uid")
    @Excel(name = "唯一uid")
    @TableId(value = "uid", type = IdType.AUTO)
    private String uid;
    /**
     * 旧文件名
     */
    @ApiModelProperty(value = "旧文件名")
    @Excel(name = "旧文件名")
    private String fileOldName;
    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    @Excel(name = "文件名")
    private String picName;
    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    @Excel(name = "文件地址")
    private String picUrl;
    /**
     * 文件扩展名
     */
    @ApiModelProperty(value = "文件扩展名")
    @Excel(name = "文件扩展名")
    private String picExpandedName;
    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    @Excel(name = "文件大小")
    private Long fileSize;
    /**
     * 文件分类uid
     */
    @ApiModelProperty(value = "文件分类uid")
    @Excel(name = "文件分类uid")
    private String fileSortUid;
    /**
     * 管理员uid
     */
    @ApiModelProperty(value = "管理员uid")
    @Excel(name = "管理员uid")
    private String adminUid;
    /**
     * 用户uid
     */
    @ApiModelProperty(value = "用户uid")
    @Excel(name = "用户uid")
    private String userUid;
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
     * 七牛云地址
     */
    @ApiModelProperty(value = "七牛云地址")
    @Excel(name = "七牛云地址")
    private String qiNiuUrl;
    /**
     * Minio文件URL
     */
    @ApiModelProperty(value = "Minio文件URL")
    @Excel(name = "Minio文件URL")
    private String minioUrl;
}
