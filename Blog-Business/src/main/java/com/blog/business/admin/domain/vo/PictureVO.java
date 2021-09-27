package com.blog.business.admin.domain.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 图片实体类
 *
 * @author yujunhong
 * @date 2021/9/24 11:53
 */
@ToString
@Data
public class PictureVO {

    /**
     * 图片UID
     */
    private String fileUid;

    /**
     * 图片UIDs
     */
    private String fileUids;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 所属相册分类UID
     */
    private String pictureSortUid;

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
