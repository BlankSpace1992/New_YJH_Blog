package com.blog.business.picture.service;

import com.blog.business.picture.domain.FileSort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author yujunhong
 * @date 2021/7/12 14:15
 */
public interface LocalFileService {

    /**
     * 多文件上传
     *
     * @param multipartFileList 文件集合
     * @param fileSort          文件分类实体
     * @return 文件存储位置
     * @throws IOException 文件处理
     * @author yujunhong
     * @date 2021/7/12 14:17
     */
    List<String> batchUploadFile(List<MultipartFile> multipartFileList, FileSort fileSort) throws IOException;

    /**
     * 文件上传
     *
     * @param multipartFile 文件
     * @param fileSort      文件分类实体
     * @return 文件存储位置
     * @throws IOException 文件处理
     * @author yujunhong
     * @date 2021/7/12 14:18
     */
    String uploadFile(MultipartFile multipartFile, FileSort fileSort) throws IOException;

    /**
     * 通过url上传图片
     *
     * @param url      文件地址
     * @param fileSort 文件分类实体
     * @return 文件存储位置
     * @author yujunhong
     * @date 2021/7/12 14:19
     */
    String uploadPictureByUrl(String url, FileSort fileSort);
}
