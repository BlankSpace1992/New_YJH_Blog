package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.CategoryMenuVO;
import com.blog.business.web.domain.CategoryMenu;
import com.blog.business.web.mapper.CategoryMenuMapper;
import com.blog.business.web.service.CategoryMenuService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Service
public class CategoryMenuServiceImpl extends ServiceImpl<CategoryMenuMapper, CategoryMenu> implements CategoryMenuService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Map<String, Object> getCategoryMenuList(CategoryMenuVO categoryMenuVO) {
        // 注入分页参数
        IPage<CategoryMenu> page = new Page<>();
        page.setSize(categoryMenuVO.getPageSize());
        page.setCurrent(categoryMenuVO.getCurrentPage());
        IPage<CategoryMenu> categoryMenuList = baseMapper.getCategoryMenuList(page, categoryMenuVO);
        // 获取实际数据
        List<CategoryMenu> list = categoryMenuList.getRecords();
        // 获取每个节点得父节点id
        List<String> ids = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getParentUid())) {
                ids.add(item.getParentUid());
            }
        });
        Map<String, Object> resultMap = new HashMap<>();
        if (ids.size() > 0) {
            List<CategoryMenu> parentList = this.listByIds(ids);
            Map<String, CategoryMenu> map = new HashMap<>();
            parentList.forEach(item -> {
                map.put(item.getUid(), item);
            });

            list.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getParentUid())) {
                    item.setParentCategoryMenu(map.get(item.getParentUid()));
                }
            });

            resultMap.put(BaseSysConf.OTHER_DATA, parentList);
        }
        categoryMenuList.setRecords(list);
        resultMap.put(BaseSysConf.DATA, categoryMenuList);
        return resultMap;
    }

    @Override
    public List<CategoryMenu> getAll(String keyword) {
        // 获取一级菜单
        LambdaQueryWrapper<CategoryMenu> firstLevelQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(keyword)) {
            firstLevelQueryWrapper.eq(CategoryMenu::getUid, keyword);
        }
        firstLevelQueryWrapper.eq(CategoryMenu::getMenuLevel, "1");
        firstLevelQueryWrapper.eq(CategoryMenu::getStatus, EnumsStatus.ENABLE);
        firstLevelQueryWrapper.eq(CategoryMenu::getMenuType, EnumsLevel.MENU);
        List<CategoryMenu> categoryMenuList = this.list(firstLevelQueryWrapper);
        //获取所有的ID，去寻找他的子目录
        List<String> idList = categoryMenuList.stream().map(CategoryMenu::getUid).collect(Collectors.toList());
        // 获取对应下的二级菜单
        LambdaQueryWrapper<CategoryMenu> childrenQueryWrapper = new LambdaQueryWrapper<>();
        childrenQueryWrapper.eq(CategoryMenu::getStatus, EnumsStatus.ENABLE);
        childrenQueryWrapper.in(CategoryMenu::getParentUid, idList);
        List<CategoryMenu> childrenMenuList = this.list(childrenQueryWrapper);
        //获取所有的二级菜单，去寻找他的子按钮
        List<String> childrenIdList = childrenMenuList.stream().map(CategoryMenu::getUid).collect(Collectors.toList());
        // 获取二级菜单下的子按钮
        LambdaQueryWrapper<CategoryMenu> buttonQueryWrapper = new LambdaQueryWrapper<>();
        buttonQueryWrapper.eq(CategoryMenu::getStatus, EnumsStatus.ENABLE);
        buttonQueryWrapper.in(CategoryMenu::getParentUid, childrenIdList);
        List<CategoryMenu> buttonMenuList = this.list(buttonQueryWrapper);

        Map<String, List<CategoryMenu>> map = new HashMap<>();
        buttonMenuList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getParentUid())) {
                List<CategoryMenu> tempList;
                if (StringUtils.isNull(map.get(item.getParentUid()))) {
                    tempList = new ArrayList<>();
                } else {
                    tempList = map.get(item.getParentUid());
                }
                tempList.add(item);
                map.put(item.getParentUid(), tempList);
            }
        });
        // 给二级菜单设置三级按钮
        childrenMenuList.forEach(item -> {
            if (map.get(item.getUid()) != null) {
                List<CategoryMenu> tempList = map.get(item.getUid());
                tempList.sort(new Comparator<CategoryMenu>() {

                    /*
                     * int compare(CategoryMenu p1, CategoryMenu p2) 返回一个基本类型的整型，
                     * 返回负数表示：p1 小于p2，
                     * 返回0 表示：p1和p2相等，
                     * 返回正数表示：p1大于p2
                     */
                    @Override
                    public int compare(CategoryMenu o1, CategoryMenu o2) {
                        //按照CategoryMenu的Sort进行降序排列
                        if (o1.getSort() > o2.getSort()) {
                            return -1;
                        }
                        if (o1.getSort().equals(o2.getSort())) {
                            return 0;
                        }
                        return 1;
                    }

                });
                item.setChildCategoryMenu(tempList);
            }
        });
        // 给一级菜单设置二级菜单
        for (CategoryMenu parentItem : categoryMenuList) {
            List<CategoryMenu> tempList = new ArrayList<>();
            for (CategoryMenu item : childrenMenuList) {
                if (item.getParentUid().equals(parentItem.getUid())) {
                    tempList.add(item);
                }
            }
            Collections.sort(tempList);
            parentItem.setChildCategoryMenu(tempList);
        }
        return categoryMenuList;
    }

    @Override
    public List<CategoryMenu> getButtonAll(String keyword) {
        // 获取一级菜单
        LambdaQueryWrapper<CategoryMenu> firstLevelQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(keyword)) {
            firstLevelQueryWrapper.eq(CategoryMenu::getUid, keyword);
        }
        firstLevelQueryWrapper.eq(CategoryMenu::getMenuLevel, "2");
        firstLevelQueryWrapper.eq(CategoryMenu::getStatus, EnumsStatus.ENABLE);
        firstLevelQueryWrapper.eq(CategoryMenu::getMenuType, EnumsLevel.MENU);
        List<CategoryMenu> categoryMenuList = this.list(firstLevelQueryWrapper);
        //获取所有的ID，去寻找他的子目录
        List<String> idList = categoryMenuList.stream().map(CategoryMenu::getUid).collect(Collectors.toList());
        // 获取二级菜单下的子按钮
        LambdaQueryWrapper<CategoryMenu> buttonQueryWrapper = new LambdaQueryWrapper<>();
        buttonQueryWrapper.eq(CategoryMenu::getStatus, EnumsStatus.ENABLE);
        buttonQueryWrapper.in(CategoryMenu::getParentUid, idList);
        List<CategoryMenu> buttonMenuList = this.list(buttonQueryWrapper);
        Set<String> secondUidSet = new HashSet<>();
        Map<String, List<CategoryMenu>> map = new HashMap<>();
        buttonMenuList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getParentUid())) {

                secondUidSet.add(item.getParentUid());

                if (map.get(item.getParentUid()) == null) {
                    List<CategoryMenu> tempList = new ArrayList<>();
                    tempList.add(item);
                    map.put(item.getParentUid(), tempList);
                } else {
                    List<CategoryMenu> tempList = map.get(item.getParentUid());
                    tempList.add(item);
                    map.put(item.getParentUid(), tempList);
                }
            }
        });
        // 过滤不在Button列表中的二级菜单
        List<CategoryMenu> secondCategoryMenuList = new ArrayList<>();
        for (CategoryMenu secondCategoryMenu : categoryMenuList) {
            for (String uid : secondUidSet) {
                if (secondCategoryMenu.getUid().equals(uid)) {
                    secondCategoryMenuList.add(secondCategoryMenu);
                    break;
                }
            }
        }

        // 给二级菜单设置三级按钮
        secondCategoryMenuList.forEach(item -> {
            if (map.get(item.getUid()) != null) {
                List<CategoryMenu> tempList = map.get(item.getUid());
                Collections.sort(tempList);
                item.setChildCategoryMenu(tempList);
            }
        });
        return categoryMenuList;
    }

    @Override
    public ResultBody add(CategoryMenuVO categoryMenuVO) {
        //如果是一级菜单，将父ID清空
        if (categoryMenuVO.getMenuLevel() == 1) {
            categoryMenuVO.setParentUid("");
        }
        CategoryMenu categoryMenu = new CategoryMenu();
        categoryMenu.setParentUid(categoryMenuVO.getParentUid());
        categoryMenu.setSort(categoryMenuVO.getSort());
        categoryMenu.setIcon(categoryMenuVO.getIcon());
        categoryMenu.setSummary(categoryMenuVO.getSummary());
        categoryMenu.setMenuLevel(categoryMenuVO.getMenuLevel());
        categoryMenu.setMenuType(categoryMenuVO.getMenuType());
        categoryMenu.setName(categoryMenuVO.getName());
        categoryMenu.setUrl(categoryMenuVO.getUrl());
        categoryMenu.setIsShow(categoryMenuVO.getIsShow());
        categoryMenu.setUpdateTime(new Date());
        categoryMenu.setIsJumpExternalUrl(categoryMenuVO.getIsJumpExternalUrl());
        this.save(categoryMenu);
        return ResultBody.success();
    }

    @Override
    public ResultBody edit(CategoryMenuVO categoryMenuVO) {
        CategoryMenu categoryMenu = this.getById(categoryMenuVO.getUid());
        if (StringUtils.isNull(categoryMenu)) {
            return ResultBody.error(BaseMessageConf.ENTITY_NOT_EXIST);
        }
        categoryMenu.setParentUid(categoryMenuVO.getParentUid());
        categoryMenu.setSort(categoryMenuVO.getSort());
        categoryMenu.setIcon(categoryMenuVO.getIcon());
        categoryMenu.setSummary(categoryMenuVO.getSummary());
        categoryMenu.setMenuLevel(categoryMenuVO.getMenuLevel());
        categoryMenu.setMenuType(categoryMenuVO.getMenuType());
        categoryMenu.setName(categoryMenuVO.getName());
        categoryMenu.setUrl(categoryMenuVO.getUrl());
        categoryMenu.setIsShow(categoryMenuVO.getIsShow());
        categoryMenu.setUpdateTime(new Date());
        categoryMenu.setIsJumpExternalUrl(categoryMenuVO.getIsJumpExternalUrl());
        this.updateById(categoryMenu);
        // 修改成功后，需要删除redis中所有的admin访问路径
        deleteAdminVisitUrl();
        return ResultBody.success();
    }

    @Override
    public ResultBody delete(CategoryMenuVO categoryMenuVO) {
        // 判断当前菜单是否有子菜单
        LambdaQueryWrapper<CategoryMenu> categoryQueryWrapper = new LambdaQueryWrapper<>();
        categoryQueryWrapper.eq(CategoryMenu::getStatus, EnumsStatus.ENABLE);
        categoryQueryWrapper.in(CategoryMenu::getParentUid, categoryMenuVO.getUid());
        int count = this.count(categoryQueryWrapper);
        if (count > 0) {
            return ResultBody.error(BaseMessageConf.CHILDREN_MENU_UNDER_THIS_MENU);
        }
        CategoryMenu categoryMenu = this.getById(categoryMenuVO.getUid());
        categoryMenu.setStatus(EnumsStatus.DISABLED);
        categoryMenu.setUpdateTime(new Date());
        this.updateById(categoryMenu);
        // 修改成功后，需要删除redis中所有的admin访问路径
        deleteAdminVisitUrl();
        return ResultBody.success();
    }

    @Override
    public ResultBody stick(CategoryMenuVO categoryMenuVO) {
        CategoryMenu categoryMenu = this.getById(categoryMenuVO.getUid());
        //查找出最大的那一个
        LambdaQueryWrapper<CategoryMenu> queryWrapper = new LambdaQueryWrapper<>();
        //如果是二级菜单 或者 按钮，就在当前的兄弟中，找出最大的一个
        if (categoryMenu.getMenuLevel() == Constants.NUM_TWO || categoryMenu.getMenuType() == EnumsLevel.BUTTON) {
            queryWrapper.eq(CategoryMenu::getParentUid, categoryMenu.getParentUid());
        }
        queryWrapper.eq(CategoryMenu::getMenuLevel, categoryMenu.getMenuLevel());
        queryWrapper.orderByDesc(CategoryMenu::getSort);
        queryWrapper.last(BaseSysConf.LIMIT_ONE);
        CategoryMenu maxSort = this.getOne(queryWrapper);
        if (StringUtils.isEmpty(maxSort.getUid())) {
            return ResultBody.error(BaseMessageConf.OPERATION_FAIL);
        }
        Integer sortCount = maxSort.getSort() + 1;
        categoryMenu.setSort(sortCount);
        categoryMenu.setUpdateTime(new Date());
        this.updateById(categoryMenu);
        return ResultBody.success();
    }


    /**
     * 删除Redis中管理员的访问路径
     *
     * @author yujunhong
     * @date 2021/9/27 10:26
     */
    private void deleteAdminVisitUrl() {
        Set<Object> keys = redisUtil.keys(BaseRedisConf.ADMIN_VISIT_MENU + "*");
        redisUtil.delete(keys);
    }
}
