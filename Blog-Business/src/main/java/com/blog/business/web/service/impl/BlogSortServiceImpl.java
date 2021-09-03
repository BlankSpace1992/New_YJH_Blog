package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.web.domain.BlogSort;
import com.blog.business.web.mapper.BlogSortMapper;
import com.blog.business.web.service.BlogSortService;
import com.blog.constants.EnumsStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class BlogSortServiceImpl extends ServiceImpl<BlogSortMapper, BlogSort> implements BlogSortService {
    @Override
    public List<BlogSort> getBlogListByClassify() {
        LambdaQueryWrapper<BlogSort> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(BlogSort::getStatus, EnumsStatus.ENABLE);
        blogWrapper.orderByDesc(BlogSort::getSort);
        return this.list(blogWrapper);
    }


}
