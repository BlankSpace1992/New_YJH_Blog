package com.blog.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.domain.Admin;
import com.blog.business.mapper.AdminMapper;
import com.blog.business.service.AdminService;
import org.springframework.stereotype.Service;
/**
 *
 * @author yujunhong
 * @date 2021/5/31 13:47
 *
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {


}
