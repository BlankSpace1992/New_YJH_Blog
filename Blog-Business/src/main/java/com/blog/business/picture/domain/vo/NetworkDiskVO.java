package com.blog.business.picture.domain.vo;

import lombok.Data;

/**
 * @author yujunhong
 * @date 2021/10/9 16:08
 */
@Data
public class NetworkDiskVO {
    /**
     * 唯一UID
     */
    private String uid;
    /**
     * 管理员UID
     */
    private String adminUid;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 旧文件名
     */
    private String fileOldName;

    /**
     * 时间戳名称
     */
    private String timestampName;

    /**
     * 扩展名
     */
    private String extendName;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 是否是目录
     */
    private Integer isDir;

    /**
     * 旧文件路径
     */
    private String oldFilePath;

    /**
     * 新文件路径
     */
    private String newFilePath;

    /**
     * 文件列表
     */
    private String files;

    /**
     * 文件类型
     */
    private Integer fileType;
}
