package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.WebNavbar;
import com.blog.business.web.mapper.WebNavbarMapper;
import com.blog.business.web.service.WebNavbarService;
import com.blog.constants.Constants;
import com.blog.constants.EnumsStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class WebNavbarServiceImpl extends ServiceImpl<WebNavbarMapper, WebNavbar> implements WebNavbarService {


    @Override
    public List<WebNavbar> getAllList() {
        // 获取导航栏信息-所有菜单
        LambdaQueryWrapper<WebNavbar> webNavbarLambdaQueryWrapper = new LambdaQueryWrapper<>();
        webNavbarLambdaQueryWrapper.eq(WebNavbar::getStatus, EnumsStatus.ENABLE);
        webNavbarLambdaQueryWrapper.orderByDesc(WebNavbar::getSort);
        List<WebNavbar> webNavbars = this.list(webNavbarLambdaQueryWrapper);
        // 筛选出一级菜单
        List<WebNavbar> firstWebNavbars =
                webNavbars.stream().filter(item -> item.getNavbarLevel().equals(Constants.STR_ONE)).collect(Collectors.toList());
        // 筛选二级菜单并按照parentUid分组
        Map<String, List<WebNavbar>> secondWebNavbarMap =
                webNavbars.stream().filter(item -> item.getNavbarLevel().equals(Constants.STR_TWO)).collect(Collectors.groupingBy(WebNavbar::getParentUid));
        // 循环一级菜单 匹配对应的二级菜单
        firstWebNavbars.forEach(item -> {
            if (secondWebNavbarMap.containsKey(item.getUid())) {
                item.setChildWebNavbar(secondWebNavbarMap.get(item.getUid()));
            }
        });
        return firstWebNavbars;
    }
}
