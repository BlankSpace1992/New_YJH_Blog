package com.blog.business.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.SystemConfig;
import com.blog.business.admin.domain.vo.SystemConfigVO;
import com.blog.business.admin.mapper.SystemConfigMapper;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.business.web.service.BlogService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private BlogService blogService;

    @Override
    public SystemConfig getSystemConfig() {
        // 优先从redis获取数据
        String result = (String) redisUtil.get(BaseRedisConf.SYSTEM_CONFIG);
        // 判断redis中是否存在资源
        if (StringUtils.isNotEmpty(result)) {
            return JSON.parseObject(result, SystemConfig.class);
        }
        // 从数据库中获取数据
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getStatus, EnumsStatus.ENABLE);
        wrapper.orderByDesc(SystemConfig::getCreateTime);
        wrapper.last(BaseSysConf.LIMIT_ONE);
        SystemConfig systemConfig =
                Optional.ofNullable(this.getOne(wrapper)).orElseThrow(() -> new CommonErrorException((BaseMessageConf.SYSTEM_CONFIG_IS_NOT_EXIST)));
        // 将数据缓存进redis
        if (StringUtils.isNotNull(systemConfig)) {
            redisUtil.set(BaseRedisConf.SYSTEM_CONFIG, JSON.toJSONString(systemConfig), 86400);
        }
        return systemConfig;
    }

    @Override
    public ResultBody cleanRedisByKey(List<String> keyList) {
        if (StringUtils.isEmpty(keyList)) {
            return ResultBody.error(BaseMessageConf.OPERATION_FAIL);
        }
        keyList.forEach(item -> {
            // 表示清空所有key
            Set<Object> keys;
            if (BaseRedisConf.ALL.equals(item)) {
                keys = redisUtil.keys(Constants.SYMBOL_STAR);
            } else {
                // 获取Redis中特定前缀
                keys = redisUtil.keys(item + Constants.SYMBOL_STAR);
            }
            redisUtil.delete(keys);
        });
        return ResultBody.success();
    }

    @Override
    public ResultBody editSystemConfig(SystemConfigVO systemConfigVO) {
        // 图片必须选择上传到一个区域
        if (OpenStatus.CLOSE.equals(systemConfigVO.getUploadLocal()) && OpenStatus.CLOSE.equals(systemConfigVO.getUploadQiNiu()) && OpenStatus.CLOSE.equals(systemConfigVO.getUploadMinio())) {
            return ResultBody.error(BaseMessageConf.PICTURE_MUST_BE_SELECT_AREA);
        }
        // 图片显示优先级为本地优先，必须开启图片上传本地
        if ((FilePriority.LOCAL.equals(systemConfigVO.getPicturePriority())
                || FilePriority.LOCAL.equals(systemConfigVO.getContentPicturePriority()))
                && OpenStatus.CLOSE.equals(systemConfigVO.getUploadLocal())) {
            return ResultBody.error(BaseMessageConf.MUST_BE_OPEN_LOCAL_UPLOAD);
        }
        // 图片显示优先级为七牛云优先，必须开启图片上传七牛云
        if ((FilePriority.QI_NIU.equals(systemConfigVO.getPicturePriority())
                || FilePriority.QI_NIU.equals(systemConfigVO.getContentPicturePriority()))
                && OpenStatus.CLOSE.equals(systemConfigVO.getUploadQiNiu())) {
            return ResultBody.error(BaseMessageConf.MUST_BE_OPEN_QI_NIU_UPLOAD);
        }
        // 图片显示优先级为Minio优先，必须开启图片上传Minio
        if ((FilePriority.MINIO.equals(systemConfigVO.getPicturePriority())
                ||FilePriority.MINIO.equals(systemConfigVO.getContentPicturePriority()))
                && OpenStatus.CLOSE.equals(systemConfigVO.getUploadMinio())) {
            return ResultBody.error(BaseMessageConf.MUST_BE_OPEN_MINIO_UPLOAD);
        }

        // 开启Email邮件通知时，必须保证Email字段不为空
        if (OpenStatus.OPEN.equals(systemConfigVO.getStartEmailNotification()) && StringUtils.isEmpty(systemConfigVO.getEmail())) {
            return ResultBody.error(BaseMessageConf.MUST_BE_SET_EMAIL);
        }
        if (StringUtils.isEmpty(systemConfigVO.getUid())) {
            SystemConfig systemConfig = new SystemConfig();
            // 设置七牛云、邮箱、系统配置相关属性【使用Spring工具类提供的深拷贝】
            BeanUtils.copyProperties(systemConfigVO, systemConfig, BaseSysConf.STATUS);
            this.save(systemConfig);
        } else {
            SystemConfig systemConfig = this.getById(systemConfigVO.getUid());
            // 设置七牛云、邮箱、系统配置相关属性【使用Spring工具类提供的深拷贝】
            BeanUtils.copyProperties(systemConfigVO, systemConfig, BaseSysConf.STATUS, BaseSysConf.UID);
            this.updateById(systemConfig);
        }
        // 更新系统配置成功后，需要删除Redis中的系统配置
        redisUtil.delete(BaseRedisConf.SYSTEM_CONFIG);
        return ResultBody.success();
    }
}
