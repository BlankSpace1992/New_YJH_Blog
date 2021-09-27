package com.blog.business.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.business.admin.domain.vo.CategoryMenuVO;
import com.blog.business.web.domain.CategoryMenu;
import org.apache.ibatis.annotations.Param;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
public interface CategoryMenuMapper extends BaseMapper<CategoryMenu> {

    /**
     * 获取菜单列表
     *
     * @param categoryMenuVO 查询条件
     * @param page           分页参数
     * @return 菜单列表
     * @author yujunhong
     * @date 2021/9/26 16:01
     */
    IPage<CategoryMenu> getCategoryMenuList(@Param("page") IPage<CategoryMenu> page,
                                            @Param("categoryMenuVO") CategoryMenuVO categoryMenuVO);
}
