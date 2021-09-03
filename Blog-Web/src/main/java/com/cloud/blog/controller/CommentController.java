package com.cloud.blog.controller;

import com.blog.business.web.domain.Comment;
import com.blog.business.web.domain.vo.CommentParamVO;
import com.blog.business.web.domain.vo.UserVO;
import com.blog.business.web.service.CommentService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.exception.ResultBody;
import com.blog.holder.RequestHolder;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private CommentService commentService;

    /**
     * 获取用户评论列表
     *
     * @param commentParamVO 查询条件vo
     * @return 用户评论信息
     * @author yujunhong
     * @date 2021/8/12 14:36
     */
    @ApiOperation(value = "获取用户评论列表")
    @PostMapping(value = "/getList")
    public ResultBody getList( @RequestBody CommentParamVO commentParamVO) {
        List<Comment> commentList = commentService.getCommentList(commentParamVO);
        return ResultBody.success(commentList);
    }

    /**
     * 获取用户的评论列表以及回复
     *
     * @param userVO 用户信息实体对象
     * @return 用户的评论列表以及回复
     * @author yujunhong
     * @date 2021/8/25 14:34
     */
    @ApiOperation(value = "获取用户的评论列表以及回复")
    @PostMapping(value = "/getListByUser")
    public ResultBody getListByUser(HttpServletRequest request, @Validated @RequestBody UserVO userVO) {
        // 判断是否存在用户id
        if (StringUtils.isNull(request.getAttribute(BaseSysConf.USER_UID))) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.INVALID_TOKEN);
        }
        String requestUserUid = request.getAttribute(BaseSysConf.USER_UID).toString();
        Map<String, Object> listByUser = commentService.getListByUser(userVO, requestUserUid);
        return ResultBody.success(listByUser);
    }

    /**
     * 获取用户点赞信息
     *
     * @param currentPage 当前页数
     * @param pageSize    每页显示数目
     * @return 用户点赞信息
     * @author yujunhong
     * @date 2021/8/25 15:22
     */
    @ApiOperation(value = "获取用户点赞信息")
    @PostMapping(value = "/getPraiseListByUser")
    public ResultBody getPraiseListByUser(@ApiParam(name = "currentPage", value = "当前页数") @RequestParam(name =
            "currentPage", required = false, defaultValue = "1") Long currentPage,
                                          @ApiParam(name = "pageSize", value = "每页显示数目") @RequestParam(name =
                                                  "pageSize", required = false, defaultValue = "10") Long pageSize) {
        HttpServletRequest request = RequestHolder.getRequest();
        if (StringUtils.isNull(request) || request.getAttribute(BaseSysConf.USER_UID) == null || request.getAttribute(BaseSysConf.TOKEN) == null) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.INVALID_TOKEN);
        }
        String userUid = request.getAttribute(BaseSysConf.USER_UID).toString();
        List<Comment> praiseListByUser = commentService.getPraiseListByUser(currentPage, pageSize, userUid);
        return ResultBody.success(praiseListByUser);
    }

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

    /**
     * 阅读用户接收的评论数
     *
     * @param request 请求
     * @return 阅读用户接收的评论数
     * @author yujunhong
     * @date 2021/8/26 15:04
     */
    @PostMapping("/readUserReceiveCommentCount")
    @ApiOperation(value = "阅读用户接收的评论数")
    public ResultBody readUserReceiveCommentCount(HttpServletRequest request) {
        // 判断用户是否登录
        if (request.getAttribute(BaseSysConf.USER_UID) != null) {
            String userUid = request.getAttribute(BaseSysConf.USER_UID).toString();
            String redisKey = BaseRedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + userUid;
            redisUtil.delete(redisKey);
        }
        return ResultBody.success("阅读成功");
    }

    /**
     * 新增评论
     *
     * @param commentVO 新增评论的实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/8/25 15:52
     */
    @ApiOperation(value = "新增评论")
    @PostMapping(value = "/add")
    public ResultBody add(@RequestBody CommentParamVO commentVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        if (StringUtils.isNull(request) || request.getAttribute(BaseSysConf.USER_UID) == null) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.INVALID_TOKEN);
        }
        String userUid = request.getAttribute(BaseSysConf.USER_UID).toString();
        commentService.add(commentVO, userUid);
        return ResultBody.success();
    }

    /**
     * 举报评论
     *
     * @param commentVO 举报评论的实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/8/26 14:05
     */
    @PostMapping(value = "/report")
    @ApiOperation(value = "举报评论")
    public ResultBody report(@RequestBody CommentParamVO commentVO) {
        commentService.report(commentVO);
        return ResultBody.success();
    }

    /**
     * 删除评论
     *
     * @param commentVO 删除评论的实体对象
     * @return ResultBody
     * @author yujunhong
     * @date 2021/8/26 14:20
     */
    @ApiOperation(value = "删除评论")
    @PostMapping(value = "/delete")
    public ResultBody delete(@RequestBody CommentParamVO commentVO) {
        commentService.delete(commentVO);
        return ResultBody.success();
    }
}
