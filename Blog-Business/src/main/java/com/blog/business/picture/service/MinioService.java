package com.blog.business.picture.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/7/8 14:10
 */
public interface MinioService {
    /**
     * 文件上传
     *
     * @param file 上传文件
     * @return 文件地址
     * @author yujunhong
     * @date 2021/7/12 13:52
     */
    String uploadFile(MultipartFile file);

    /**
     * 通过url上传图片
     *
     * @param url 图片url地址
     * @return 上传状态
     * @author yujunhong
     * @date 2021/7/12 13:53
     */
    String uploadPictureByUrl(String url);

    /**
     * 多个文件上传
     *
     * @param multipartFileList 多个文件集合
     * @return 文件地址
     * @author yujunhong
     * @date 2021/7/12 13:54
     */
    List<String> batchUploadFile(List<MultipartFile> multipartFileList);
}
