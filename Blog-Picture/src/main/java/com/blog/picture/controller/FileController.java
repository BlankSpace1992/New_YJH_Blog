package com.blog.picture.controller;

import com.blog.business.picture.service.FileService;
import com.blog.utils.FeignUtils;
import com.blog.utils.MinIoUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yujunhong
 * @date 2021/6/22 09:51
 */
@RestController
@RequestMapping(value = "/file")
@Api(value = "文件服务相关接口", tags = "文件服务相关接口")
public class FileController {
    @Autowired
    private MinIoUtils minIoUtils;
    @Autowired
    private FeignUtils feignUtils;
    @Autowired
    private FileService fileService;

    
}
