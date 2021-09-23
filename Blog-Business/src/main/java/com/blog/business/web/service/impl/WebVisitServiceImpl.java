package com.blog.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.WebVisit;
import com.blog.business.web.mapper.WebVisitMapper;
import com.blog.business.web.service.WebVisitService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.Constants;
import com.blog.utils.DateUtils;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class WebVisitServiceImpl extends ServiceImpl<WebVisitMapper, WebVisit> implements WebVisitService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Integer getWebVisitCount(Date startDate, Date endDate) {
        return baseMapper.getWebVisitCount(startDate, endDate);
    }

    @Override
    public Map<String, Object> getVisitByWeek() {
        // 优先从Redis中获取一周访问数量
        String resultJson =
                (String) redisUtil.get(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.WEEK_VISIT);
        // 若不为空则直接返回
        if (StringUtils.isNotEmpty(resultJson)) {
            return JSON.parseObject(resultJson, new TypeReference<Map<String, Object>>() {
            });
        }
        // 获取今天结束时间
        String endDate = DateUtils.parseDateToStr("yyyy-MM-dd", DateUtils.getNowDate()) + " 23:59:59";
        // 获取七天前的时间
        String startDate = DateUtils.parseDateToStr("yyyy-MM-dd", DateUtils.getDate(endDate, -6)) + "00:00:00";
        // 获取最近七天的数组列表
        List<String> sevenDaysList = DateUtils.getDaysByArray(7, "yyyy-MM-dd");
        // 获取最近7天的每天访问量
        List<Map<String, Object>> webVisitByWeek = baseMapper.getWebVisitByWeek(startDate, endDate);
        // 获取最近7天独立用户的每天数量
        List<Map<String, Object>> webVisitUserByWeek = baseMapper.getWebVisitUserByWeek(startDate, endDate);
        // 拆分集合,统一map存放数据
        Map<String, Object> countWebVisitMap = new HashMap<>();
        Map<String, Object> countWebVisitUserMap = new HashMap<>();

        for (Map<String, Object> item : webVisitByWeek) {
            countWebVisitMap.put(item.get("date").toString(), item.get("count"));
        }
        for (Map<String, Object> item : webVisitUserByWeek) {
            countWebVisitUserMap.put(item.get("date").toString(), item.get("count"));
        }
        // 访问量数组
        List<Integer> webVisitList = new ArrayList<>();
        // 独立用户数组
        List<Integer> wenVisitUserList = new ArrayList<>();
        // 根据天数进行拆分
        for (String day : sevenDaysList) {
            if (countWebVisitMap.get(day) != null) {
                Number pvNumber = (Number) countWebVisitMap.get(day);
                Number uvNumber = (Number) countWebVisitUserMap.get(day);
                webVisitList.add(pvNumber.intValue());
                wenVisitUserList.add(uvNumber.intValue());
            } else {
                webVisitList.add(0);
                wenVisitUserList.add(0);
            }
        }
        Map<String, Object> resultMap = new HashMap<>(Constants.NUM_THREE);
        // 不含年份的数组格式
        List<String> resultSevenDaysList = DateUtils.getDaysByArray(7, "MM-dd");
        resultMap.put("date", resultSevenDaysList);
        resultMap.put("pv", webVisitList);
        resultMap.put("uv", wenVisitUserList);
        // 存放redis
        redisUtil.set(BaseRedisConf.DASHBOARD + Constants.SYMBOL_COLON + BaseRedisConf.WEEK_VISIT,
                JSON.toJSONString(resultMap), 10 * 60);
        return resultMap;
    }
}
