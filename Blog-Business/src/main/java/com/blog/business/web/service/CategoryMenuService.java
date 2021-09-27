package com.blog.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.business.admin.domain.vo.CategoryMenuVO;
import com.blog.business.web.domain.CategoryMenu;
import com.blog.exception.ResultBody;

import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface CategoryMenuService extends IService<CategoryMenu> {

    /**
     * 获取菜单列表
     *
     * @param categoryMenuVO 查询条件
     * @return 菜单列表
     * @author yujunhong
     * @date 2021/9/26 16:01
     */
    Map<String, Object> getCategoryMenuList(CategoryMenuVO categoryMenuVO);

    /**
     * 获取所有菜单列表
     *
     * @param keyword 关键词
     * @return 所有菜单列表
     * @author yujunhong
     * @date 2021/9/27 9:58
     */
    List<CategoryMenu> getAll(String keyword);


    /**
     * 获取所有二级菜单-按钮列表
     *
     * @param keyword 关键词
     * @return 获取所有二级菜单-按钮列表
     * @author yujunhong
     * @date 2021/9/27 10:17
     */
    List<CategoryMenu> getButtonAll(String keyword);

    /**
     * 增加菜单
     *
     * @param categoryMenuVO 增加菜单实体
     * @return 增加菜单
     * @author yujunhong
     * @date 2021/9/27 10:21
     */
    ResultBody add(CategoryMenuVO categoryMenuVO);

    /**
     * 编辑菜单
     *
     * @param categoryMenuVO 编辑菜单实体
     * @return 编辑菜单
     * @author yujunhong
     * @date 2021/9/27 10:21
     */
    ResultBody edit(CategoryMenuVO categoryMenuVO);

    /**
     * 删除菜单
     *
     * @param categoryMenuVO 删除菜单实体
     * @return 删除菜单
     * @author yujunhong
     * @date 2021/9/27 10:21
     */
    ResultBody delete(CategoryMenuVO categoryMenuVO);

    /**
     * 删除菜单
     *
     * @param categoryMenuVO 删除菜单实体
     * @return 删除菜单
     * @author yujunhong
     * @date 2021/9/27 10:21
     */
    ResultBody stick(CategoryMenuVO categoryMenuVO);
}
