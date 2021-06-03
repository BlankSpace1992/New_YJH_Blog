package com.blog.business.picture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.picture.domain.File;
import com.blog.business.picture.mapper.FileMapper;
import com.blog.business.picture.service.FileService;
import org.springframework.stereotype.Service;
/**
 *
 * @author yujunhong
 * @date 2021/6/3 11:57
 *
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService{

}
