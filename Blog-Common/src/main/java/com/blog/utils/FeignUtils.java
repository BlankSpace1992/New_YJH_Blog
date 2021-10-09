package com.blog.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.entity.SystemConfigCommon;
import com.blog.exception.CommonErrorException;
import com.blog.feign.AdminFeignClient;
import com.blog.feign.WebFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Feign操作工具类
 *
 * @author yujunhong
 * @date 2021/6/22 10:31
 */
@Component
public class FeignUtils {

    @Autowired
    private AdminFeignClient adminFeignClient;
    @Autowired
    private WebFeignClient webFeignClient;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 通过token获取系统配置 返回map类型
     *
     * @param token 值
     * @return 通过token获取系统配置
     * @author yujunhong
     * @date 2021/6/22 10:34
     */
    public Map<String, String> getSystemConfigMap(String token) {
        // 判断token的有效性
        String adminJsonResult = (String) redisUtil.get(BaseRedisConf.LOGIN_TOKEN_KEY + Constants.SYMBOL_COLON + token);
        if (StringUtils.isEmpty(adminJsonResult)) {
            throw new CommonErrorException(ErrorCode.INVALID_TOKEN, BaseMessageConf.INVALID_TOKEN);
        }
        // 从Redis中获取的SystemConf 或者 通过feign获取的
        Map<String, String> resultMap = new HashMap<>();
        // 优先从Redis中获取系统配置内容 如果没有则通过feign获取
        String systemConfigResult = (String) redisUtil.get(BaseRedisConf.SYSTEM_CONFIG);
        if (StringUtils.isEmpty(systemConfigResult)) {
            resultMap = JSON.parseObject(systemConfigResult, new TypeReference<Map<String, String>>() {
            });
        } else {
            // 通过feign获取系统配置
            Object result = adminFeignClient.getSystemConfig().getResult();
            resultMap = JSON.parseObject(JSON.toJSONString(result), new TypeReference<Map<String, String>>() {

            });
            // 将数据存储进redis 30分钟过期
            redisUtil.set(BaseRedisConf.SYSTEM_CONFIG, JSON.toJSONString(resultMap), 1800);
        }
        return resultMap;
    }

    /**
     * 获取系统配置类型 无论是Admin端还是Web端
     *
     * @return 配置信息
     * @author yujunhong
     * @date 2021/7/1 14:18
     */
    public SystemConfigCommon getSystemConfig() {
        ServletRequestAttributes attribute =
                Optional.of((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).orElseThrow(() -> new CommonErrorException(ErrorCode.PLEASE_SET_QI_NIU, BaseMessageConf.PLEASE_HAVE_REQUEST));
        // 获取请求
        HttpServletRequest request =
                Optional.of(attribute.getRequest()).orElseThrow(() -> new CommonErrorException(ErrorCode.PLEASE_SET_QI_NIU, BaseMessageConf.PLEASE_HAVE_REQUEST));
        // 后台携带的token
        Object token = request.getAttribute(BaseSysConf.TOKEN);
        // 参数中携带的token
        String parameterToken = request.getParameter(BaseSysConf.TOKEN);
        // 获取属于平台 web:门户 admin:后台
        String platform = request.getParameter(BaseSysConf.PLATFORM);
        // 系统配置文件map
        Map<String, String> systemConfigMap = new HashMap<>();
        // 判断是否为web端发送过来的请求---后端发送的token长度为32
        if (BaseSysConf.WEB.equals(platform) || (StringUtils.isNotNull(parameterToken) && parameterToken.length() == Constants.THIRTY_TWO)) {
            // 如果是调用web端获取配置的接口
            systemConfigMap = this.getSystemConfigByWebToken(parameterToken);
        } else {
            // 调用admin端获取配置端口
            if (StringUtils.isNotNull(token)) {
                systemConfigMap = this.getSystemConfigMap(String.valueOf(token));
            } else {
                systemConfigMap = this.getSystemConfigMap(parameterToken);
            }
        }
        if (StringUtils.isNull(systemConfigMap)) {
            throw new CommonErrorException(ErrorCode.PLEASE_SET_QI_NIU, BaseMessageConf.PLEASE_SET_QI_NIU);
        }

        SystemConfigCommon systemConfig = new SystemConfigCommon();
        // 图片是否上传七牛云
        String uploadQiNiu = systemConfigMap.get(BaseSysConf.UPLOAD_QI_NIU);
        // 是否上传本地保存
        String uploadLocal = systemConfigMap.get(BaseSysConf.UPLOAD_LOCAL);
        // 本地图片地址
        String localPictureBaseUrl = systemConfigMap.get(BaseSysConf.LOCAL_PICTURE_BASE_URL);
        // 七牛云图片地址
        String qiNiuPictureBaseUrl = systemConfigMap.get(BaseSysConf.QI_NIU_PICTURE_BASE_URL);
        // 七牛云公钥
        String qiNiuAccessKey = systemConfigMap.get(BaseSysConf.QI_NIU_ACCESS_KEY);
        // 七牛云私钥
        String qiNiuSecretKey = systemConfigMap.get(BaseSysConf.QI_NIU_SECRET_KEY);
        // 七牛云上传空间
        String qiNiuBucket = systemConfigMap.get(BaseSysConf.QI_NIU_BUCKET);
        // 七牛云存储区域 华东（z0），华北(z1)，华南(z2)，北美(na0)，东南亚(as0)
        String qiNiuArea = systemConfigMap.get(BaseSysConf.QI_NIU_AREA);
        // minio 远程连接地址
        String minioEndPoint = systemConfigMap.get(BaseSysConf.MINIO_END_POINT);
        // minio 公钥
        String minioAccessKey = systemConfigMap.get(BaseSysConf.MINIO_ACCESS_KEY);
        // minio 私钥
        String minioSecretKey = systemConfigMap.get(BaseSysConf.MINIO_SECRET_KEY);
        // minio 上传空间
        String minioBucket = systemConfigMap.get(BaseSysConf.MINIO_BUCKET);
        // 文件是否上传minio
        String uploadMinio = systemConfigMap.get(BaseSysConf.UPLOAD_MINIO);
        // minio图片存储地址
        String minioPictureBaseUrl = systemConfigMap.get(BaseSysConf.MINIO_PICTURE_BASE_URL);

        // 判断七牛云的参数是否存在异常
        if (OpenStatus.OPEN.equals(uploadQiNiu)
                && (StringUtils.isEmpty(qiNiuPictureBaseUrl)
                || StringUtils.isEmpty(qiNiuAccessKey)
                || StringUtils.isEmpty(qiNiuSecretKey)
                || StringUtils.isEmpty(qiNiuBucket)
                || StringUtils.isEmpty(qiNiuArea))) {
            throw new CommonErrorException(ErrorCode.PLEASE_SET_QI_NIU, BaseMessageConf.PLEASE_SET_QI_NIU);
        }
        // 判断本地服务参数是否存在异常
        if (OpenStatus.OPEN.equals(uploadLocal) && StringUtils.isEmpty(localPictureBaseUrl)) {
            throw new CommonErrorException(ErrorCode.PLEASE_SET_LOCAL, BaseMessageConf.PLEASE_SET_QI_NIU);
        }
        // 判断minio服务是否存在异常
        if (OpenStatus.OPEN.equals(uploadMinio) && (StringUtils.isEmpty(minioEndPoint)
                || StringUtils.isEmpty(minioPictureBaseUrl)
                || StringUtils.isEmpty(minioAccessKey)
                || StringUtils.isEmpty(minioSecretKey)
                || StringUtils.isEmpty(minioBucket))) {
            throw new CommonErrorException(ErrorCode.PLEASE_SET_MINIO, BaseMessageConf.PLEASE_SET_MINIO);
        }
        systemConfig.setQiNiuAccessKey(qiNiuAccessKey);
        systemConfig.setQiNiuSecretKey(qiNiuSecretKey);
        systemConfig.setQiNiuBucket(qiNiuBucket);
        systemConfig.setQiNiuArea(qiNiuArea);
        systemConfig.setUploadQiNiu(uploadQiNiu);
        systemConfig.setUploadLocal(uploadLocal);
        // 图片显示优先级（ 1 展示 七牛云,  0 本地）
        systemConfig.setPicturePriority(systemConfigMap.get(BaseSysConf.PICTURE_PRIORITY));
        systemConfig.setLocalPictureBaseUrl(localPictureBaseUrl);
        systemConfig.setQiNiuPictureBaseUrl(qiNiuPictureBaseUrl);

        systemConfig.setMinioEndPoint(minioEndPoint);
        systemConfig.setMinioAccessKey(minioAccessKey);
        systemConfig.setMinioSecretKey(minioSecretKey);
        systemConfig.setMinioBucket(minioBucket);
        systemConfig.setMinioPictureBaseUrl(minioPictureBaseUrl);
        systemConfig.setUploadMinio(uploadMinio);
        return systemConfig;
    }

    /**
     * 从map中获取系统配置文件
     *
     * @param systemConfigMap 系统配置map
     * @return 系统配置实体类
     * @author yujunhong
     * @date 2021/7/7 13:48
     */
    public SystemConfigCommon getSystemConfigByMap(Map<String, String> systemConfigMap) {
        SystemConfigCommon systemConfig = new SystemConfigCommon();
        if (StringUtils.isNull(systemConfigMap)) {
            throw new CommonErrorException(ErrorCode.SYSTEM_CONFIG_NOT_EXIST, BaseMessageConf.SYSTEM_CONFIG_NOT_EXIST);
        }
        // 图片是否上传七牛云
        String uploadQiNiu = systemConfigMap.get(BaseSysConf.UPLOAD_QI_NIU);
        // 是否上传本地保存
        String uploadLocal = systemConfigMap.get(BaseSysConf.UPLOAD_LOCAL);
        // 本地图片地址
        String localPictureBaseUrl = systemConfigMap.get(BaseSysConf.LOCAL_PICTURE_BASE_URL);
        // 七牛云图片地址
        String qiNiuPictureBaseUrl = systemConfigMap.get(BaseSysConf.QI_NIU_PICTURE_BASE_URL);
        // 七牛云公钥
        String qiNiuAccessKey = systemConfigMap.get(BaseSysConf.QI_NIU_ACCESS_KEY);
        // 七牛云私钥
        String qiNiuSecretKey = systemConfigMap.get(BaseSysConf.QI_NIU_SECRET_KEY);
        // 七牛云上传空间
        String qiNiuBucket = systemConfigMap.get(BaseSysConf.QI_NIU_BUCKET);
        // 七牛云存储区域 华东（z0），华北(z1)，华南(z2)，北美(na0)，东南亚(as0)
        String qiNiuArea = systemConfigMap.get(BaseSysConf.QI_NIU_AREA);
        // minio 远程连接地址
        String minioEndPoint = systemConfigMap.get(BaseSysConf.MINIO_END_POINT);
        // minio 公钥
        String minioAccessKey = systemConfigMap.get(BaseSysConf.MINIO_ACCESS_KEY);
        // minio 私钥
        String minioSecretKey = systemConfigMap.get(BaseSysConf.MINIO_SECRET_KEY);
        // minio 上传空间
        String minioBucket = systemConfigMap.get(BaseSysConf.MINIO_BUCKET);
        // 文件是否上传minio
        String uploadMinio = systemConfigMap.get(BaseSysConf.UPLOAD_MINIO);
        // minio图片存储地址
        String minioPictureBaseUrl = systemConfigMap.get(BaseSysConf.MINIO_PICTURE_BASE_URL);

        // 判断七牛云的参数是否存在异常
        if (OpenStatus.OPEN.equals(uploadQiNiu)
                && StringUtils.isEmpty(qiNiuPictureBaseUrl)
                || StringUtils.isEmpty(qiNiuAccessKey)
                || StringUtils.isEmpty(qiNiuSecretKey)
                || StringUtils.isEmpty(qiNiuBucket)
                || StringUtils.isEmpty(qiNiuArea)) {
            throw new CommonErrorException(ErrorCode.PLEASE_SET_QI_NIU, BaseMessageConf.PLEASE_SET_QI_NIU);
        }
        // 判断本地服务参数是否存在异常
        if (OpenStatus.OPEN.equals(uploadLocal) && StringUtils.isEmpty(localPictureBaseUrl)) {
            throw new CommonErrorException(ErrorCode.PLEASE_SET_LOCAL, BaseMessageConf.PLEASE_SET_QI_NIU);
        }
        // 判断minio服务是否存在异常
        if (OpenStatus.OPEN.equals(uploadMinio) && (StringUtils.isEmpty(minioEndPoint)
                || StringUtils.isEmpty(minioPictureBaseUrl)
                || StringUtils.isEmpty(minioAccessKey)
                || StringUtils.isEmpty(minioSecretKey)
                || StringUtils.isEmpty(minioBucket))) {
            throw new CommonErrorException(ErrorCode.PLEASE_SET_MINIO, BaseMessageConf.PLEASE_SET_MINIO);
        }
        systemConfig.setQiNiuAccessKey(qiNiuAccessKey);
        systemConfig.setQiNiuSecretKey(qiNiuSecretKey);
        systemConfig.setQiNiuBucket(qiNiuBucket);
        systemConfig.setQiNiuArea(qiNiuArea);
        systemConfig.setUploadQiNiu(uploadQiNiu);
        systemConfig.setUploadLocal(uploadLocal);
        // 图片显示优先级（ 1 展示 七牛云,  0 本地）
        systemConfig.setPicturePriority(systemConfigMap.get(BaseSysConf.PICTURE_PRIORITY));
        systemConfig.setLocalPictureBaseUrl(localPictureBaseUrl);
        systemConfig.setQiNiuPictureBaseUrl(qiNiuPictureBaseUrl);

        systemConfig.setMinioEndPoint(minioEndPoint);
        systemConfig.setMinioAccessKey(minioAccessKey);
        systemConfig.setMinioSecretKey(minioSecretKey);
        systemConfig.setMinioBucket(minioBucket);
        systemConfig.setMinioPictureBaseUrl(minioPictureBaseUrl);
        systemConfig.setUploadMinio(uploadMinio);

        return systemConfig;
    }

    /**
     * 通过Web端token获取系统配置文件[传入Admin端得token]
     *
     * @param token token值
     * @return 系统配置map
     * @author yujunhong
     * @date 2021/7/7 13:53
     */
    public Map<String, String> getSystemConfigByWebToken(String token) {
        // 判断token得有效性
        String tokenResult = (String) redisUtil.get(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token);
        if (StringUtils.isEmpty(tokenResult)) {
            throw new CommonErrorException(ErrorCode.INVALID_TOKEN, BaseMessageConf.INVALID_TOKEN);
        }
        // 通过从redis获取SystemConfig,若redis不存在则通过feign获取
        Map<String, String> resultMap = new HashMap<>();
        // 优先从redis获取内容
        String systemConfigResult = (String) redisUtil.get(BaseRedisConf.SYSTEM_CONFIG);
        // 如果结果不为空则直接返回
        if (StringUtils.isNotEmpty(systemConfigResult)) {
            return JSON.parseObject(systemConfigResult, new TypeReference<Map<String, String>>() {
            });
        }
        // 从webFeignClient获取
        String systemConfig = webFeignClient.getSystemConfig(token);
        // 转换为map
        Map<String, String> map = JSON.parseObject(systemConfig, new TypeReference<Map<String, String>>() {
        });
        // 判断查询结果返回码以及数据是否为空
        if (StringUtils.isNotNull(map.get(BaseSysConf.CODE)) && BaseSysConf.SUCCESS.equals(map.get(BaseSysConf.CODE))) {
            resultMap = JSON.parseObject(map.get(BaseSysConf.DATA), new TypeReference<Map<String, String>>() {
            });
            // 讲此系统配置反存进redis中
            redisUtil.set(BaseRedisConf.SYSTEM_CONFIG, JSON.toJSONString(resultMap), 18000);
        }
        return resultMap;
    }
}
