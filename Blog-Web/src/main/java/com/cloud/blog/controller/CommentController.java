package com.cloud.blog.controller;

import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 评论相关接口 controller
 *
 * @author yujunhong
 * @date 2021/6/1 10:47
 */
@RestController
@RequestMapping(value = "/comment")
@Api(value = "002 - 评论相关接口", tags = "002 - 评论相关接口")
public class CommentController {
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取用户收到的评论回复数
     *
     * @param httpRequest 请求
     * @return 获取用户收到的评论回复数
     * @author yujunhong
     * @date 2021/6/2 16:08
     */
    @ApiOperation(value = "获取用户收到的评论回复数")
    @GetMapping(value = "/getUserReceiveCommentCount")
    public ResultBody getUserReceiveCommentCount(HttpServletRequest httpRequest) {
        // 判断用户是否登录
        int commentCount = 0;
        Object attribute = httpRequest.getAttribute(BaseSysConf.USER_UID);
        if (StringUtils.isNotNull(attribute)) {
            String uid = attribute.toString();
            String redisKey = BaseRedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + uid;
            String count = (String) redisUtil.get(redisKey);
            if (StringUtils.isNotNull(count)) {
                commentCount = Integer.parseInt(count);
            }
        }
        return ResultBody.success(commentCount);
    }
}
