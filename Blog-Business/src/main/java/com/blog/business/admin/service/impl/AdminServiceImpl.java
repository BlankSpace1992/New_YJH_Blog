package com.blog.business.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.Admin;
import com.blog.business.admin.mapper.AdminMapper;
import com.blog.business.admin.service.AdminService;
import com.blog.business.utils.WebUtils;
import com.blog.constants.Constants;
import com.blog.feign.PictureFeignClient;
import com.blog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yujunhong
 * @date 2021/5/31 13:47
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebUtils webUtils;

    @Override
    public Admin getAdminByUserName(String username) {
        // 根据用户查询数据
        Admin admin = baseMapper.getAdminByUsername(username);
        // 判空
        if (StringUtils.isNull(admin)) {
            return null;
        }
        // 获取用户头像
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            admin.setPhotoList(webUtils.getPicture(pictureFeignClient.getPicture(admin.getAvatar(),
                    Constants.SYMBOL_COMMA)));
        }
        return admin;
    }
}

