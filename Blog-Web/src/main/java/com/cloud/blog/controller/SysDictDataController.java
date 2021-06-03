package com.cloud.blog.controller;

import com.blog.business.web.service.SysDictDataService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页相关接口 controller
 *
 * @author yujunhong
 * @date 2021/6/1 10:47
 */
@RestController
@RequestMapping(value = "/sysDictData")
@Api(value = "003 - 数据字典相关接口", tags = "003 - 数据字典相关接口")
public class SysDictDataController {
    @Autowired
    private SysDictDataService sysDictDataService;

    /**
     * 根据字典类型数组获取字典数据
     *
     * @param dictTypeList 字典类型集合
     * @return 字典数据
     * @author yujunhong
     * @date 2021/6/2 16:28
     */
    @ApiOperation(value = "根据字典类型数组获取字典数据")
    @PostMapping(value = "/getListByDictTypeList")
    public ResultBody getListByDictTypeList(@RequestBody List<String> dictTypeList) {
        return ResultBody.success(sysDictDataService.getListByDictTypeList(dictTypeList));
    }
}
