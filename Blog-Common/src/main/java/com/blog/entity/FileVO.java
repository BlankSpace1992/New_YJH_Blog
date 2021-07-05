package com.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 文件图片类
 *
 * @author yujunhong
 * @date 2021/6/22 9:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {
    /**
     * 唯一UID
     */
    private String uid;
    /**
     * 如果是用户上传，则包含用户uid
     */
    private String userUid;

    /**
     * 如果是管理员上传，则包含管理员uid
     */
    private String adminUid;

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 模块名
     */
    private String sortName;

    /**
     * 图片Url集合
     */
    private List<String> urlList;

    /**
     * 系统配置
     */
    private Map<String, String> systemConfig;

    /**
     * 上传图片时携带的token令牌
     */
    private String token;

}
