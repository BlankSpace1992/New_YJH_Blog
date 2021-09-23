package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.business.web.domain.WebVisit;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface WebVisitMapper extends BaseMapper<WebVisit> {
    /**
     * 获取时间段内访问数量
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间段内访问数量
     * @author yujunhong
     * @date 2021/9/22 16:42
     */
    Integer getWebVisitCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


    /**
     * 获取最近七天每天的访问量
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 最近七天每天的访问量
     * @author yujunhong
     * @date 2021/9/22 17:06
     */
    List<Map<String, Object>> getWebVisitByWeek(@Param("startDate") String startDate, @Param("endDate") String endDate);


    /**
     * 获取最近七天每天的用户数
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 获取最近七天每天的用户数
     * @author yujunhong
     * @date 2021/9/22 17:06
     */
    List<Map<String, Object>> getWebVisitUserByWeek(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
