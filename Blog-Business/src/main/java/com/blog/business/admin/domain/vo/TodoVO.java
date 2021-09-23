package com.blog.business.admin.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yujunhong
 * @date 2021/9/23 11:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoVO {
    /**
     * 唯一UID
     */
    private String uid;

    /**
     * 内容
     */
    private String text;
    /**
     * 表示事项是否完成
     */
    private Boolean done;

    /**
     * 当前页
     */
    private Long currentPage;

    /**
     * 页大小
     */
    private Long pageSize;

    /**
     * 关键字
     */
    private String keyword;

}
