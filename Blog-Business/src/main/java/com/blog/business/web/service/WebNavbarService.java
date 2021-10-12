package com.blog.business.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.WebNavbarVO;
import com.blog.business.web.domain.WebNavbar;
import com.blog.exception.ResultBody;

import java.util.List;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface WebNavbarService extends IService<WebNavbar> {

    /**
     * 获取导航栏信息
     *
     * @return 获取导航栏信息
     * @author yujunhong
     * @date 2021/6/2 14:30
     */
    List<WebNavbar> getAllList();

    /**
     * 获取门户导航栏列表
     *
     * @param webNavbarVO 查询条件
     * @return 获取门户导航栏列表
     * @author yujunhong
     * @date 2021/10/11 15:56
     */
    IPage<WebNavbar> getPageList(WebNavbarVO webNavbarVO);


    /**
     * 增加门户导航栏
     *
     * @param webNavbarVO 实体对象
     * @return 增加门户导航栏
     * @author yujunhong
     * @date 2021/10/11 16:04
     */
    ResultBody add(WebNavbarVO webNavbarVO);


    /**
     * 编辑门户导航栏
     *
     * @param webNavbarVO 实体对象
     * @return 编辑门户导航栏
     * @author yujunhong
     * @date 2021/10/11 16:04
     */
    ResultBody edit(WebNavbarVO webNavbarVO);

    /**
     * 删除门户导航栏
     *
     * @param webNavbarVO 实体对象
     * @return 删除门户导航栏
     * @author yujunhong
     * @date 2021/10/11 16:04
     */
    ResultBody delete(WebNavbarVO webNavbarVO);
}
