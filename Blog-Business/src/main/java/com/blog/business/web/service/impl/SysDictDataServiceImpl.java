package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.SysDictData;
import com.blog.business.web.domain.SysDictType;
import com.blog.business.web.mapper.SysDictDataMapper;
import com.blog.business.web.service.SysDictDataService;
import com.blog.business.web.service.SysDictTypeService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseSysConf;
import com.blog.constants.EnumsStatus;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysDictTypeService sysDictTypeService;

    @Override
    public Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        List<String> tempTypeList = new ArrayList<>();
        // 循环字典类型 优先从redis中获取
        dictTypeList.forEach(item -> {
            //从Redis中获取内容
            String jsonResult =
                    (String) redisUtil.get(BaseSysConf.REDIS_DICT_TYPE + BaseSysConf.REDIS_SEGMENTATION + item);
            //判断redis中是否有字典
            if (StringUtils.isNotEmpty(jsonResult)) {
                Map<String, Object> tempMap = JSON.parseObject(jsonResult);
                map.put(item, tempMap);
            } else {
                // 如果redis中没有该字典，那么从数据库中查询
                tempTypeList.add(item);
            }
        });
        // 表示数据全部从redis中获取到了，直接返回即可
        if (tempTypeList.size() <= 0) {
            return map;
        }
        // 查询 dict_type 在 tempTypeList中的 数据库
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysDictType::getDictType, tempTypeList);
        wrapper.eq(SysDictType::getStatus, EnumsStatus.ENABLE);
        wrapper.eq(SysDictType::getIsPublish, EnumsStatus.PUBLISH);
        List<SysDictType> sysDictTypes = sysDictTypeService.list(wrapper);
        sysDictTypes.forEach(item -> {
            LambdaQueryWrapper<SysDictData> sysDictDataQueryWrapper = new LambdaQueryWrapper<>();
            sysDictDataQueryWrapper.eq(SysDictData::getIsPublish, EnumsStatus.PUBLISH);
            sysDictDataQueryWrapper.eq(SysDictData::getStatus, EnumsStatus.ENABLE);
            sysDictDataQueryWrapper.eq(SysDictData::getDictTypeUid, item.getUid());
            sysDictDataQueryWrapper.orderByDesc(SysDictData::getCreateTime);
            List<SysDictData> list = this.list(sysDictDataQueryWrapper);
            String defaultValue = null;
            for (SysDictData sysDictData : list) {
                // 获取默认值
                if (sysDictData.getIsDefault() == BaseSysConf.ONE) {
                    defaultValue = sysDictData.getDictValue();
                    break;
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put(BaseSysConf.DEFAULT_VALUE, defaultValue);
            result.put(BaseSysConf.LIST, list);
            map.put(item.getDictType(), result);
            redisUtil.set(BaseSysConf.REDIS_DICT_TYPE + BaseSysConf.REDIS_SEGMENTATION + item.getDictType(),
                    JSON.toJSON(result).toString(), 86400);
        });
        return map;
    }
}
