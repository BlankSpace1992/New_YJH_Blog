package com.blog.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.domain.WebNavbar;

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
}
