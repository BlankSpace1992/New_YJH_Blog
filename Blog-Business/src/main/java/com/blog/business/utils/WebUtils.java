package com.blog.business.utils;

import com.blog.business.admin.domain.SystemConfig;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.constants.BaseSysConf;
import com.blog.constants.FilePriority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * web有关的工具类
 *
 * @author yujunhong
 * @date 2021/8/9 14:16
 */
@Component
@Slf4j
public class WebUtils {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 格式化数据获取图片列表
     *
     * @param result 图片列表字符串
     * @return 图片地址集合
     * @author yujunhong
     * @date 2021/8/9 14:17
     */
    public List<String> getPicture(List<Map<String, Object>> result) {
        // 获取配置信息
        SystemConfig systemConfig = systemConfigService.getsSystemConfig();
        // 图片优先级
        String picturePriority = systemConfig.getPicturePriority();
        // 本地图片地址
        String localPictureBaseUrl = systemConfig.getLocalPictureBaseUrl();
        // 七牛云图片地址
        String qiNiuPictureBaseUrl = systemConfig.getQiNiuPictureBaseUrl();
        // minio图片地址
        String minioPictureBaseUrl = systemConfig.getMinioPictureBaseUrl();
        //  图片地址集合
        List<String> picUrls = new ArrayList<>();
        // 循环遍历
        for (Map<String, Object> stringObjectMap : result) {
            // 判断文件显示优先级【需要显示存储在哪里的图片】
            if (FilePriority.QI_NIU.equals(picturePriority)) {
                picUrls.add(qiNiuPictureBaseUrl + stringObjectMap.get(BaseSysConf.QI_NIU_URL));
            } else if (FilePriority.MINIO.equals(picturePriority)) {
                picUrls.add(minioPictureBaseUrl + stringObjectMap.get(BaseSysConf.MINIO_URL));
            } else {
                picUrls.add(localPictureBaseUrl + stringObjectMap.get(BaseSysConf.URL));
            }
        }
        return picUrls;
    }

    /**
     * 获取图片,返回map
     *
     * @param result 图片列表字符串
     * @return 图片地址map集合, 根据用户id分组
     * @author yujunhong
     * @date 2021/8/9 14:32
     */
    public List<Map<String, Object>> getPictureMap(List<Map<String, Object>> result) {
        // 获取配置信息
        SystemConfig systemConfig = systemConfigService.getsSystemConfig();
        // 图片优先级
        String picturePriority = systemConfig.getPicturePriority();
        // 本地图片地址
        String localPictureBaseUrl = systemConfig.getLocalPictureBaseUrl();
        // 七牛云图片地址
        String qiNiuPictureBaseUrl = systemConfig.getQiNiuPictureBaseUrl();
        // minio图片地址
        String minioPictureBaseUrl = systemConfig.getMinioPictureBaseUrl();
        List<Map<String, Object>> resultList = new ArrayList<>();
        // 循环遍历
        for (Map<String, Object> stringObjectMap : result) {
            Map<String, Object> map = new HashMap<>();
            if (StringUtils.isEmpty(stringObjectMap.get(BaseSysConf.UID))) {
                continue;
            }
            // 判断文件显示优先级【需要显示存储在哪里的图片】
            if (FilePriority.QI_NIU.equals(picturePriority)) {
                map.put(BaseSysConf.URL, qiNiuPictureBaseUrl + stringObjectMap.get(BaseSysConf.QI_NIU_URL));
            } else if (FilePriority.MINIO.equals(picturePriority)) {
                map.put(BaseSysConf.URL, minioPictureBaseUrl + stringObjectMap.get(BaseSysConf.MINIO_URL));
            } else {
                map.put(BaseSysConf.URL, localPictureBaseUrl + stringObjectMap.get(BaseSysConf.URL));
            }
            map.put(BaseSysConf.UID, stringObjectMap.get(BaseSysConf.UID));
            resultList.add(map);
        }
        return resultList;
    }

}
