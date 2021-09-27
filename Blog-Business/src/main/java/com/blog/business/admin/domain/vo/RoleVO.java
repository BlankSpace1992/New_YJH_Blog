package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
  * @author yujunhong
  * @date 2021/9/26 15:18
  */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleVO  {
    /**
     * 唯一UID
     */
    private String uid;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 介绍
     */
    private String summary;

    /**
     * 该角色所能管辖的区域
     */
    private String categoryMenuUids;


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
