package com.blog.business.admin.domain.vo;

import lombok.Data;

/**
  * @author yujunhong
  * @date 2021/9/27 14:27
  */
@Data
public class SysParamsVO {

    /**
     * 唯一UID
     */
    private String uid;
    /**
     * 参数名称
     */
    private String paramsName;

    /**
     * 参数键名
     */
    private String paramsKey;

    /**
     * 参数键值
     */
    private String paramsValue;

    /**
     * 参数类型，是否系统内置（1：是，0：否）
     */
    private Integer paramsType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;

}
