package com.blog.business.picture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.picture.domain.Storage;
import com.blog.business.picture.mapper.StorageMapper;
import com.blog.business.picture.service.StorageService;
import org.springframework.stereotype.Service;
/**
 *
 * @author yujunhong
 * @date 2021/6/3 11:57
 *
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService{

}
