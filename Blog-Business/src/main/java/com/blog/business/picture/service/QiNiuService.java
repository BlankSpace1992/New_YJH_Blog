package com.blog.business.picture.service;

import com.blog.entity.SystemConfigCommon;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/7/8 14:09
 */
public interface QiNiuService {
    /**
     * 多文件上传
     *
     * @param multipartFiles 文件集合
     * @return 文件上传地址
     * @author yujunhong
     * @date 2021/7/8 14:18
     */
    List<String> batchUploadFile(List<MultipartFile> multipartFiles);

    /**
     * 文件上传
     *
     * @param multipartFile 需上传文件
     * @author yujunhong
     * @date 2021/7/8 14:19
     */
    String uploadFile(MultipartFile multipartFile);

    /**
     * 通过URL上传图片
     *
     * @param url          地址
     * @param systemConfig 系统配置
     * @return 文件上传地址
     * @author yujunhong
     * @date 2021/7/8 14:20
     */
    String uploadPictureByUrl(String url, SystemConfigCommon systemConfig);
}
