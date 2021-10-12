package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.WebNavbarVO;
import com.blog.business.web.domain.WebNavbar;
import com.blog.business.web.mapper.WebNavbarMapper;
import com.blog.business.web.service.WebNavbarService;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.constants.EnumsStatus;
import com.blog.exception.ResultBody;
import org.springframework.beans.BeanUtils;
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

    @Override
    public IPage<WebNavbar> getPageList(WebNavbarVO webNavbarVO) {
        // 注入分页参数
        IPage<WebNavbar> page = new Page<>();
        page.setCurrent(webNavbarVO.getCurrentPage());
        page.setSize(webNavbarVO.getPageSize());
        return baseMapper.getPageList(page, webNavbarVO);
    }

    @Override
    public ResultBody add(WebNavbarVO webNavbarVO) {
        //如果是一级菜单，将父ID清空
        if ("1".equals(webNavbarVO.getNavbarLevel())) {
            webNavbarVO.setParentUid("");
        }
        WebNavbar webNavbar = new WebNavbar();
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(webNavbarVO, webNavbar, BaseSysConf.STATUS);
        webNavbar.setStatus(EnumsStatus.ENABLE);
        this.save(webNavbar);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(WebNavbarVO webNavbarVO) {
        //如果是一级菜单，将父ID清空
        if ("1".equals(webNavbarVO.getNavbarLevel())) {
            webNavbarVO.setParentUid("");
        }
        WebNavbar webNavbar = this.getById(webNavbarVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(webNavbarVO, webNavbar, BaseSysConf.STATUS, BaseSysConf.UID);
        this.updateById(webNavbar);
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(WebNavbarVO webNavbarVO) {
        // 查询当前菜单下是否有子菜单
        LambdaQueryWrapper<WebNavbar> webNavbarWrapper = new LambdaQueryWrapper<>();
        webNavbarWrapper.eq(WebNavbar::getStatus, EnumsStatus.ENABLE);
        webNavbarWrapper.eq(WebNavbar::getParentUid, webNavbarVO.getUid());
        int count = this.count(webNavbarWrapper);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.CHILDREN_MENU_UNDER_THIS_MENU);
        }
        WebNavbar webNavbar = this.getById(webNavbarVO.getUid());
        webNavbar.setStatus(EnumsStatus.DISABLED);
        this.updateById(webNavbar);
        return ResultBody.success();
    }
}
