package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.vo.SysDictTypeVO;
import com.blog.business.web.domain.SysDictType;
import com.blog.business.web.mapper.SysDictTypeMapper;
import com.blog.business.web.service.SysDictTypeService;
import org.springframework.stereotype.Service;
/**
 *
 * @author yujunhong
 * @date 2021/6/1 11:05
 *
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {


    @Override
    public IPage<SysDictType> getPageList(SysDictTypeVO sysDictTypeVO) {
        // 注入分页参数
        IPage<SysDictType> page = new Page<>();
        page.setCurrent(sysDictTypeVO.getCurrentPage());
        page.setSize(sysDictTypeVO.getPageSize());

        return baseMapper.getPageList(page, sysDictTypeVO);
    }
}
