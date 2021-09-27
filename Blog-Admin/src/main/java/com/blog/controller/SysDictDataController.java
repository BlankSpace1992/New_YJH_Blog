package com.blog.controller;

import com.blog.business.web.service.SysDictDataService;
import com.blog.exception.ResultBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/9/24 09:52
 */
@RestController
@RequestMapping(value = "/sysDictData")
@Api(value = "字典数据相关接口", tags = {"字典数据相关接口"})
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

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据
     * @author yujunhong
     * @date 2021/9/24 16:31
     */
    @ApiOperation(value = "根据字典类型获取字典数据", notes = "根据字典类型获取字典数据", response = String.class)
    @PostMapping("/getListByDictType")
    public ResultBody getListByDictType(@RequestParam("dictType") String dictType) {
        Map<String, Object> listByDictType =
                sysDictDataService.getListByDictType(dictType);
        return ResultBody.success(listByDictType);
    }
}
