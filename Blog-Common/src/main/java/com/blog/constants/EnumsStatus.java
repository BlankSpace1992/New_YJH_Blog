package com.blog.constants;

/**
 * 状态枚举类
 *
 * @author yujunhong
 * @date 2021/5/31 13:57
 */
public class EnumsStatus {
    /**
     * 删除的
     */
    public static final int DISABLED = 0;
    /**
     * 激活的
     */
    public static final int ENABLE = 1;
    /**
     * 冻结的
     */
    public static final int FREEZE = 2;
    /**
     * 置顶的
     */
    public static final int STICK = 3;

    /**
     * 发布
     */
    public static final String PUBLISH = "1";

    /**
     * 下架
     */
    public static final String NO_PUBLISH = "0";

    /**
     * 评论
     */
    public static final Integer COMMENT = 0;

    /**
     * 点赞
     */
    public static final Integer PRAISE = 1;

    /**
     * 关闭
     */
    public static final String CLOSE = "0";
    /**
     * 开启
     */
    public static final String OPEN = "1";

    /**
     * 关闭
     */
    public static final Integer CLOSE_STATUS = 0;
    /**
     * 开启
     */
    public static final Integer OPEN_STATUS = 1;

    /**
     * 原创
     */
    public static final String ORIGINAL = "1";

    /**
     * 非原创
     */
    public static final String UNORIGINAL = "0";
}
