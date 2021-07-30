package com.blog.utils;

import com.alibaba.fastjson.JSON;
import com.blog.constants.BaseSysConf;
import com.blog.entity.SystemConfigCommon;
import com.blog.enums.QiNiuArea;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/7/8 14:39
 */
@Slf4j
@Component
public class QiNiuUtils {
    @Autowired
    private FeignUtils feignUtils;

    /**
     * 七牛云上传图片
     *
     * @param localFile   上传文件
     * @param qiNiuConfig 七牛云配置
     * @return 七牛云服务器路径
     * @author yujunhong
     * @date 2021/7/8 14:49
     */
    public String uploadQiNiu(File localFile, Map<String, String> qiNiuConfig) throws QiniuException {
        // 获取存储配置类
        Configuration configuration = setQiNiuArea(qiNiuConfig.get(BaseSysConf.QI_NIU_AREA));
        //生成上传凭证，然后准备上传
        String accessKey = qiNiuConfig.get(BaseSysConf.QI_NIU_ACCESS_KEY);
        String secretKey = qiNiuConfig.get(BaseSysConf.QI_NIU_SECRET_KEY);
        String bucket = qiNiuConfig.get(BaseSysConf.QI_NIU_BUCKET);

        UploadManager uploadManager = new UploadManager(configuration);
        String key = StringUtils.getUUID();
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        Response response = uploadManager.put(localFile, key, upToken);

        //解析上传成功的结果
        DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
        log.info("{七牛图片上传key: " + putRet.key + ",七牛图片上传hash: " + putRet.hash + "}");
        return putRet.key;
    }

    /**
     * 七牛云上传图片
     *
     * @param localFile    上传文件
     * @param systemConfig 系统配置
     * @return 七牛云服务器路径
     * @author yujunhong
     * @date 2021/7/8 14:59
     */
    public String uploadQiNiu(File localFile, SystemConfigCommon systemConfig) throws QiniuException {
        // 获取存储配置类
        Configuration configuration = setQiNiuArea(systemConfig.getQiNiuArea());
        //生成上传凭证，然后准备上传
        String accessKey = systemConfig.getQiNiuAccessKey();
        String secretKey = systemConfig.getQiNiuSecretKey();
        String bucket = systemConfig.getQiNiuBucket();

        UploadManager uploadManager = new UploadManager(configuration);
        String key = StringUtils.getUUID();
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        Response response = uploadManager.put(localFile, key, upToken);

        //解析上传成功的结果
        DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
        log.info("{七牛图片上传key: " + putRet.key + ",七牛图片上传hash: " + putRet.hash + "}");
        return putRet.key;
    }

    /**
     * 删除七牛云文件
     *
     * @param fileName    文件名称
     * @param qiNiuConfig 七牛云配置文件 map
     * @return 删除标志
     * @author yujunhong
     * @date 2021/7/12 13:43
     */
    public int deleteFile(String fileName, Map<String, String> qiNiuConfig) {
        // 构造一个指定zone对象得配置类
        Configuration configuration = setQiNiuArea(qiNiuConfig.get(BaseSysConf.QI_NIU_AREA));
        // 获取上传凭证
        String accessKey = qiNiuConfig.get(BaseSysConf.QI_NIU_ACCESS_KEY);
        String secretKey = qiNiuConfig.get(BaseSysConf.QI_NIU_SECRET_KEY);
        String bucket = qiNiuConfig.get(BaseSysConf.QI_NIU_BUCKET);
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, configuration);
        try {
            Response delete = bucketManager.delete(bucket, fileName);
            log.info("{七牛云文件 {} 删除成功", fileName);
            return delete.statusCode;
        } catch (Exception e) {
            //如果遇到异常，说明删除失败
            log.error(e.getMessage());
        }
        return -1;
    }

    /**
     * 批量删除七牛云照片
     *
     * @param fileNameList 文件名称集合
     * @param qiNiuConfig  七牛云配置文件 map
     * @return 删除标志
     * @author yujunhong
     * @date 2021/7/12 13:47
     */
    public boolean deleteFileList(List<String> fileNameList, Map<String, String> qiNiuConfig) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = setQiNiuArea(qiNiuConfig.get(BaseSysConf.QI_NIU_AREA));
        //获取上传凭证
        String accessKey = qiNiuConfig.get(BaseSysConf.QI_NIU_ACCESS_KEY);
        String secretKey = qiNiuConfig.get(BaseSysConf.QI_NIU_SECRET_KEY);
        String bucket = qiNiuConfig.get(BaseSysConf.QI_NIU_BUCKET);
        int successCount = 0;
        for (String fileName : fileNameList) {
            String key = fileName;
            Auth auth = Auth.create(accessKey, secretKey);
            BucketManager bucketManager = new BucketManager(auth, cfg);
            try {
                Response delete = bucketManager.delete(bucket, key);
                log.info("{七牛云文件 {} 删除成功", fileName);
                successCount += 1;
            } catch (QiniuException ex) {
                //如果遇到异常，说明删除失败
                log.error(ex.getMessage());
            }
        }
        return successCount == fileNameList.size();
    }

    /**
     * 设置七牛云上传区域
     *
     * @param area 区域名称
     * @return 七牛云存储配置
     * @author yujunhong
     * @date 2021/7/8 14:51
     */
    private Configuration setQiNiuArea(String area) {
        //构造一个带指定Zone对象的配置类
        Configuration configuration = null;
        switch (QiNiuArea.valueOf(area).getCode()) {
            case "z0": {
                configuration = new Configuration(Zone.zone0());
            }
            break;
            case "z1": {
                configuration = new Configuration(Zone.zone1());
            }
            break;
            case "z2": {
                configuration = new Configuration(Zone.zone2());
            }
            break;
            case "na0": {
                configuration = new Configuration(Zone.zoneNa0());
            }
            break;
            case "as0": {
                configuration = new Configuration(Zone.zoneAs0());
            }
            break;
            default: {
                return null;
            }
        }
        return configuration;
    }
}
