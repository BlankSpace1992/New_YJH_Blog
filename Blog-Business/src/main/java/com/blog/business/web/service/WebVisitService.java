package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.web.domain.WebVisit;

import java.util.Date;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface WebVisitService extends IService<WebVisit> {

    /**
     * 获取时间段内访问数量
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间段内访问数量
     * @author yujunhong
     * @date 2021/9/22 16:42
     */
    Integer getWebVisitCount(Date startDate, Date endDate);

    /**
     * 最近一周用户独立IP数和访问量
     * date: ["2019-6-20","2019-6-21","2019-6-22","2019-6-23","2019-6-24",,"2019-6-25","2019-6-26"]
     * pv: [10,5,6,7,5,3,2]
     * uv: [5,3,4,4,5,2,1]
     * 注：PV表示访问量   UV表示独立用户数
     *
     * @return 最近一周用户独立IP数和访问量
     * @author yujunhong
     * @date 2021/9/22 16:52
     */
    Map<String, Object> getVisitByWeek();
}
