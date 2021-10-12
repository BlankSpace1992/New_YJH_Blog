package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.WebNavbarVO;
import com.blog.business.web.domain.WebNavbar;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface WebNavbarMapper extends BaseMapper<WebNavbar> {

    /**
     * 获取门户导航栏列表
     *
     * @param webNavbarVO 查询条件
     * @param page        分页参数
     * @return 获取门户导航栏列表
     * @author yujunhong
     * @date 2021/10/11 15:56
     */
    IPage<WebNavbar> getPageList(@Param("page") IPage<WebNavbar> page, @Param("webNavbarVO") WebNavbarVO webNavbarVO);
}
