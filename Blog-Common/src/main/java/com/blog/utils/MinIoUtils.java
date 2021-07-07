package com.blog.utils;

import com.alibaba.fastjson.JSON;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.Constants;
import com.blog.entity.SystemConfigCommon;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地对象存储服务 Minio上传工具类
 *
 * @author yujunhong
 * @date 2021/6/22 09:56
 */
@Component
@Slf4j
public class MinIoUtils {

    @Autowired
    private FeignUtils feignUtils;

    /**
     * 文件上传
     *
     * @param file 上传文件
     * @return 文件上传路径
     * @author yujunhong
     * @date 2021/7/7 15:42
     */
    public String uploadFile(MultipartFile file) {
        return uploadSingleFile(file);
    }

    /**
     * 批量文件上传
     *
     * @param files 多个文件集合
     * @return 文件路径
     * @author yujunhong
     * @date 2021/7/7 15:48
     */
    public String batchUploadFile(List<MultipartFile> files) {
        List<String> urlList = new ArrayList<>();
        for (MultipartFile file : files) {
            urlList.add(uploadSingleFile(file));
        }
        return JSON.toJSONString(urlList);
    }

    /**
     * 删除单个文件
     *
     * @param fileName 文件名
     * @return 删除成功标志
     * @author yujunhong
     * @date 2021/7/7 15:51
     */
    public String deleteFile(String fileName) {
        // 获取系统配置
        SystemConfigCommon systemConfig = feignUtils.getSystemConfig();
        // 创建minioClient
        MinioClient minioClient = new MinioClient(systemConfig.getMinioEndPoint(), systemConfig.getMinioAccessKey(),
                systemConfig.getMinioSecretKey());
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(systemConfig.getMinioBucket()).object(fileName).build());
        } catch (Exception e) {
            log.error("删除Minio中的文件失败 fileName: {}, 错误消息: {}", fileName, e.getMessage());
            e.printStackTrace();
        }
        return BaseMessageConf.DELETE_SUCCESS;
    }

    /** 批量删除文件
      * @author yujunhong
      * @date 2021/7/7 15:56
      * @param fileNames 多个文件名称集合
      * @return 删除成功标志
      */
    public String deleteBatchFile(List<String> fileNames) {
        // 获取系统配置
        SystemConfigCommon systemConfig = feignUtils.getSystemConfig();
        MinioClient minioClient = new MinioClient(systemConfig.getMinioEndPoint(), systemConfig.getMinioAccessKey(), systemConfig.getMinioSecretKey());
        try {
            for (String fileName : fileNames) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(systemConfig.getMinioBucket()).object(fileName).build());
            }
        } catch (Exception e) {
            log.error("批量删除文件失败, 错误消息: {}", e.getMessage());
            return BaseMessageConf.DELETE_DEFAULT_ERROR;
        }
        return BaseMessageConf.DELETE_SUCCESS;
    }

    /**
     * 上传单个文件 返回上传成功后得地址
     *
     * @param multipartFile 上传文件
     * @return 文件存储地址
     * @author yujunhong
     * @date 2021/7/7 14:24
     */
    private String uploadSingleFile(MultipartFile multipartFile) {
        String url = StringUtils.EMPTY;
        try {
            // 使用minio服务得url 端口,accessKey,SecretKey创建一个minioClient得对象
            SystemConfigCommon systemConfig = new SystemConfigCommon();
            // 创建client
            MinioClient minioClient = new MinioClient(systemConfig.getMinioEndPoint(),
                    systemConfig.getMinioAccessKey(), systemConfig.getMinioSecretKey());
            // 获取文件名称
            String originalFilename = multipartFile.getOriginalFilename();
            // 获取扩展名,默认是jpg
            String expandedName = FileUtils.getExpandedName(originalFilename);
            // 获取新文件名-保存在服务器中得名称
            String currentFilename = System.currentTimeMillis() + Constants.SYMBOL_POINT + expandedName;
            // 重新生成一个文件
            InputStream inputStream = multipartFile.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(systemConfig.getMinioBucket()).object(currentFilename).stream(
                            inputStream, multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType())
                            .build());
            url = Constants.SYMBOL_LEFT_OBLIQUE_LINE + systemConfig.getMinioBucket() + Constants.SYMBOL_LEFT_OBLIQUE_LINE + currentFilename;
        } catch (Exception e) {
            e.getStackTrace();
            log.error("上传文件异常:{}", e.getMessage());
        }
        return url;
    }
}
