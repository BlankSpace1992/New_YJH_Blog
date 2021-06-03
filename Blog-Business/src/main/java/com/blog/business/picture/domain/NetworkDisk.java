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
 * 网盘文件表
 *
 * @author yujunhong
 * @date 2021/06/03 11:58:14
 */
@ApiModel(value = "网盘文件表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_network_disk")
public class NetworkDisk {
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
     * 扩展名
     */
    @ApiModelProperty(value = "扩展名")
    @Excel(name = "扩展名")
    private String extendName;
    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    @Excel(name = "文件名")
    private String fileName;
    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径")
    @Excel(name = "文件路径")
    private String filePath;
    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    @Excel(name = "文件大小")
    private Long fileSize;
    /**
     * 是否目录
     */
    @ApiModelProperty(value = "是否目录")
    @Excel(name = "是否目录")
    private Integer isDir;
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
     * 本地文件URL
     */
    @ApiModelProperty(value = "本地文件URL")
    @Excel(name = "本地文件URL")
    private String localUrl;
    /**
     * 七牛云URL
     */
    @ApiModelProperty(value = "七牛云URL")
    @Excel(name = "七牛云URL")
    private String qiNiuUrl;
    /**
     * 上传前文件名
     */
    @ApiModelProperty(value = "上传前文件名")
    @Excel(name = "上传前文件名")
    private String fileOldName;
    /**
     * Minio文件URL
     */
    @ApiModelProperty(value = "Minio文件URL")
    @Excel(name = "Minio文件URL")
    private String minioUrl;
}
