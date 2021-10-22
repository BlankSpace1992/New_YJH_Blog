package com.blog.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.business.admin.domain.SystemConfig;
import com.blog.business.admin.domain.vo.CommentVO;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.business.utils.WebUtils;
import com.blog.business.web.domain.*;
import com.blog.business.web.domain.vo.CommentParamVO;
import com.blog.business.web.domain.vo.UserVO;
import com.blog.business.web.mapper.CommentMapper;
import com.blog.business.web.service.*;
import com.blog.config.rabbit_mq.RabbitMqUtils;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.enums.EnumCommentSource;
import com.blog.exception.CommonErrorException;
import com.blog.exception.ResultBody;
import com.blog.feign.PictureFeignClient;
import com.blog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yujunhong
 * @date 2021/6/1 11:05
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private UserService userService;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private BlogService blogService;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CommentReportService commentReportService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private WebUtils webUtils;
    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
    private Long userTokenSurvivalTime;
    @Value(value = "${data.website.url}")
    private String dataWebsiteUrl;
    @Autowired
    private RabbitMqUtils rabbitMqUtils;

    @Override
    public IPage<Comment> getPageList(CommentVO commentVO) {
        // 注入分页参数
        IPage<Comment> page = new Page<>();
        page.setCurrent(commentVO.getCurrentPage());
        page.setSize(commentVO.getPageSize());
        IPage<Comment> pageList = baseMapper.getPageList(page, commentVO);
        // 获取实际数据
        List<Comment> commentList = pageList.getRecords();
        // 获取对应用户以及博客uid
        Set<String> userUidSet = new HashSet<>();
        Set<String> blogUidSet = new HashSet<>();
        commentList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                userUidSet.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                userUidSet.add(item.getToUserUid());
            }
            if (StringUtils.isNotEmpty(item.getBlogUid())) {
                blogUidSet.add(item.getBlogUid());
            }
        });
        // 获取博客
        List<Blog> blogList = new ArrayList<>();
        if (blogUidSet.size() > 0) {
            blogList = blogService.listByIds(blogUidSet);
        }
        Map<String, Blog> blogMap = new HashMap<>();
        blogList.forEach(item -> {
            // 评论管理并不需要查看博客内容，因此将其排除
            item.setContent("");
            blogMap.put(item.getUid(), item);
        });
        // 获取用户
        List<User> userCollection = new ArrayList<>();
        if (userUidSet.size() > 0) {
            userCollection = userService.listByIds(userUidSet);
        }
        // 获取头像
        StringBuilder fileUidBuilder = new StringBuilder();
        userCollection.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUidBuilder.append(item.getAvatar()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(fileUidBuilder.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        Map<String, String> pictureMap = new HashMap<>();
        picture = webUtils.getPictureMap(picture);
        picture.forEach(item -> {
            pictureMap.put(item.get(BaseSysConf.UID).toString(), item.get(BaseSysConf.URL).toString());
        });
        Map<String, User> userMap = new HashMap<>();
        userCollection.forEach(item -> {
            // 判断头像是否为空
            if (pictureMap.get(item.getAvatar()) != null) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });
        for (Comment item : commentList) {

            try {
                EnumCommentSource commentSource = EnumCommentSource.valueOf(item.getSource());
                item.setSourceName(commentSource.getName());
            } catch (Exception e) {
                log.error("ECommentSource 转换异常");
            }

            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }
            if (StringUtils.isNotEmpty(item.getBlogUid())) {
                item.setBlog(blogMap.get(item.getBlogUid()));
            }
        }
        pageList.setRecords(commentList);
        return pageList;
    }

    @Override
    public IPage<Comment> getCommentList(CommentParamVO commentParamVO) {
        // 查询当前博客的评论信息
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(commentParamVO.getBlogUid())) {
            wrapper.eq(Comment::getBlogUid, commentParamVO.getBlogUid());
        }
        Page<Comment> page = new Page<>();
        page.setCurrent(commentParamVO.getCurrentPage());
        page.setSize(commentParamVO.getPageSize());
        wrapper.eq(Comment::getSource, commentParamVO.getSource());
        wrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
        // 查询出所有一级评论
        wrapper.isNull(Comment::getToUid);
        wrapper.eq(Comment::getType, EnumsStatus.COMMENT);
        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> commentPage = this.page(page, wrapper);
        List<Comment> comments = commentPage.getRecords();
        // 获取所有评论的uid-第一级评论
        List<String> uidList = comments.stream().map(Comment::getUid).collect(Collectors.toList());
        // 查询所有的子评论
        if (StringUtils.isNotEmpty(uidList)) {
            wrapper.clear();
            wrapper.in(Comment::getFirstCommentUid, uidList);
            wrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
            List<Comment> notFirstCommentList = this.list(wrapper);
            comments.addAll(notFirstCommentList);
        }
        // 获取回复用户的评论
        List<String> userUidList = new ArrayList<>();
        comments.forEach(item -> {
            String userUid = item.getUserUid();
            String toUserUid = item.getToUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                userUidList.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(toUserUid)) {
                userUidList.add(item.getToUserUid());
            }
        });
        // 获取用户信息
        List<User> userList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userUidList)) {
            userList = userService.getUserListByIds(userUidList);
        }
        // 获取用户头像
        StringBuilder avatarString = new StringBuilder();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                avatarString.append(item.getAvatar()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> pictureList = pictureFeignClient.getPicture(avatarString.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        List<Map<String, Object>> realPictureList = webUtils.getPictureMap(pictureList);
        // 拆分图片
        Map<String, String> pictureMap = new HashMap<>();
        realPictureList.forEach(item -> pictureMap.put(item.get(BaseSQLConf.UID).toString(),
                item.get(BaseSQLConf.URL).toString()));

        // 获取用户信息并设置用户头像
        Map<String, User> userMap = new HashMap<>();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && StringUtils.isNotNull(pictureMap.get(item.getAvatar()))) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });
        // 获取评价中用户以及回复用户信息
        Map<String, Comment> commentMap = new HashMap<>();
        comments.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }
            commentMap.put(item.getUid(), item);
        });
        // 筛选出toUid存在的评论
        List<Comment> toUidCommentList =
                comments.stream().filter(item -> StringUtils.isNotEmpty(item.getToUid())).collect(Collectors.toList());
        // 评论按照toUid进行分组
        Map<String, List<Comment>> commentToUidMap =
                toUidCommentList.stream().collect(Collectors.groupingBy(Comment::getToUid));
        // 筛选toUid为空得数据--无回复数据
        List<Comment> firstCommentList =
                comments.stream().filter(item -> StringUtils.isEmpty(item.getToUid())).collect(Collectors.toList());
        List<Comment> commentReply = getCommentReply(firstCommentList, commentToUidMap);
        commentPage.setRecords(commentReply);
        return commentPage;
    }

    @Override
    public Map<String, Object> getListByUser(UserVO userVO, String userUid) {
        // 分页查询
        Page<Comment> page = new Page<>();
        page.setCurrent(userVO.getCurrentPage());
        page.setSize(userVO.getPageSize());
        // 查询当前用户的评论以及回复
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getType, EnumsStatus.COMMENT);
        commentWrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
        commentWrapper.and(wrapper -> wrapper.eq(Comment::getUserUid, userUid).or().eq(Comment::getToUserUid, userUid));
        commentWrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> commentPage = this.page(page, commentWrapper);
        List<Comment> commentList = commentPage.getRecords();
        // 获取评论中用户id以及回复用户id
        List<String> userIdList = new ArrayList<>();
        commentList.forEach(item -> {
            String commentUserUid = item.getUserUid();
            String toUserUid = item.getToUserUid();
            if (StringUtils.isNotEmpty(commentUserUid)) {
                userIdList.add(commentUserUid);
            }
            if (StringUtils.isNotEmpty(toUserUid)) {
                userIdList.add(toUserUid);
            }
        });
        // 获取用户信息
        List<User> userList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userIdList)) {
            userList = userService.getUserListByIds(userIdList);
        }
        // 获取用户头像
        StringBuilder avatarString = new StringBuilder();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                avatarString.append(item.getAvatar()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> pictureList = pictureFeignClient.getPicture(avatarString.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        pictureList = webUtils.getPictureMap(pictureList);
        // 拆分图片
        Map<String, String> pictureMap = new HashMap<>();
        pictureList.forEach(item -> pictureMap.put(item.get(BaseSQLConf.UID).toString(),
                item.get(BaseSQLConf.URL).toString()));
        // 获取用户信息并设置用户头像
        Map<String, User> userMap = new HashMap<>();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && StringUtils.isNotNull(pictureMap.get(item.getAvatar()))) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });
        // 将评论列表划分为 我的评论 和 我的回复
        List<Comment> userCommentList = new ArrayList<>();
        List<Comment> replyList = new ArrayList<>();
        commentList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }

            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }
            // 设置sourceName
            if (StringUtils.isNotEmpty(item.getSource())) {
                try {
                    item.setSourceName(EnumCommentSource.valueOf(item.getSource()).getName());
                } catch (Exception e) {
                    log.error("EnumCommentSource转换异常");
                }
            }
            if (userUid.equals(item.getUserUid())) {
                userCommentList.add(item);
            }
            if (userUid.equals(item.getToUserUid())) {
                replyList.add(item);
            }
        });
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(BaseSysConf.COMMENT_LIST, userCommentList);
        resultMap.put(BaseSysConf.REPLY_LIST, replyList);
        return resultMap;
    }

    @Override
    public List<Comment> getPraiseListByUser(Long currentPage, Long pageSize, String userUid) {
        // 分页查询
        Page<Comment> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(currentPage);
        // 查询点赞信息
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getUserUid, userUid);
        commentWrapper.eq(Comment::getType, EnumsStatus.PRAISE);
        commentWrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
        commentWrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = this.page(page, commentWrapper);
        List<Comment> commentList = commentPage.getRecords();
        // 获取点赞对应的博客
        List<String> blogUidList = commentList.stream().map(Comment::getBlogUid).collect(Collectors.toList());
        // 获取博客信息
        Map<String, Blog> blogMap = new HashMap<>();
        if (blogUidList.size() > 0) {
            List<Blog> blogList = blogService.listByIds(blogUidList);
            blogList.forEach(blog -> {
                // 并不需要content内容
                blog.setContent("");
                blogMap.put(blog.getUid(), blog);
            });
        }
        commentList.forEach(item -> {
            if (blogMap.get(item.getBlogUid()) != null) {
                item.setBlog(blogMap.get(item.getBlogUid()));
            }
        });
        return commentList;
    }

    @Override
    public ResultBody add(CommentParamVO commentVO, String userUid) {
        // 获取网站配置信息
        LambdaQueryWrapper<WebConfig> webConfigWrapper = new LambdaQueryWrapper<>();
        webConfigWrapper.eq(WebConfig::getStatus, EnumsStatus.ENABLE);
        WebConfig webConfig = webConfigService.getOne(webConfigWrapper);
        // 判断是狗开启全局评论功能
        if (BaseSysConf.CAN_NOT_COMMENT.equals(webConfig.getOpenComment())) {
            return ResultBody.error(BaseMessageConf.NO_COMMENTS_OPEN);
        }
        // 判断当前博客是否开启评论功能
        if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
            Blog blog = blogService.getById(commentVO.getBlogUid());
            if (BaseSysConf.CAN_NOT_COMMENT.equals(blog.getOpenComment())) {
                return ResultBody.error(BaseMessageConf.NO_COMMENTS_OPEN);
            }
        }
        // 获取用户信息
        User user = userService.getById(userUid);
        // 判断字数是否超过限制
        if (commentVO.getContent().length() > BaseSysConf.ONE_ZERO_TWO_FOUR) {
            return ResultBody.error(BaseMessageConf.COMMENT_CAN_NOT_MORE_THAN_1024);
        }
        // 判断该用户是否被禁言
        if (user.getCommentStatus() == BaseSysConf.ZERO) {
            return ResultBody.error(BaseMessageConf.YOU_DONT_HAVE_PERMISSION_TO_SPEAK);
        }
        // 判断是否发送过多无意义评论
        Object jsonResult =
                redisUtil.get(BaseRedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid);
        if (StringUtils.isNotNull(jsonResult)) {
            int count = Integer.parseInt(String.valueOf(jsonResult));
            if (count >= Constants.NUM_FIVE) {
                return ResultBody.error(BaseMessageConf.PLEASE_TRY_AGAIN_IN_AN_HOUR);
            }
        }
        // 判断是否垃圾评论
        String content = commentVO.getContent();
        if (StringUtils.isCommentSpam(content)) {
            if (StringUtils.isNotNull(jsonResult)) {
                int count = 0;
                redisUtil.set(BaseRedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid, count, 3600);
            } else {
                redisUtil.increment(BaseRedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid
                        , 1);
            }
            return ResultBody.error(BaseMessageConf.COMMENT_IS_SPAM);
        }
        if (StringUtils.isNotEmpty(commentVO.getToUserUid())) {
            User toUser = userService.getById(commentVO.getToUserUid());
            if (toUser.getStartEmailNotification() == BaseSysConf.ONE) {
                Comment toComment = this.getById(commentVO.getToUid());
                if (StringUtils.isNotNull(toComment) && StringUtils.isNotEmpty(toComment.getContent())) {
                    Map<String, String> map = new HashMap<>();
                    map.put(BaseSysConf.EMAIL, toUser.getEmail());
                    map.put(BaseSysConf.TEXT, commentVO.getContent());
                    map.put(BaseSysConf.TO_TEXT, toComment.getContent());
                    map.put(BaseSysConf.NICKNAME, user.getNickName());
                    map.put(BaseSysConf.TO_NICKNAME, toUser.getNickName());
                    map.put(BaseSysConf.USER_UID, toUser.getUid());
                    String url = getUrlByCommentSource(commentVO);
                    map.put(BaseSysConf.URL, url);
                    // 发送评论邮件
                    rabbitMqUtils.sendCommentEmail(map);
                }

            }
        }
        Comment comment = new Comment();
        comment.setSource(commentVO.getSource());
        comment.setBlogUid(commentVO.getBlogUid());
        comment.setContent(commentVO.getContent());
        comment.setToUserUid(commentVO.getToUserUid());

        // 当该评论不是一级评论时，需要设置一级评论UID字段
        if (StringUtils.isNotEmpty(commentVO.getToUid())) {
            Comment toComment = this.getById(commentVO.getToUid());
            // 表示 toComment是非一级评论
            if (toComment != null && StringUtils.isNotEmpty(toComment.getFirstCommentUid())) {
                comment.setFirstCommentUid(toComment.getFirstCommentUid());
            } else {
                // 表示父评论是一级评论，直接获取UID
                comment.setFirstCommentUid(toComment.getUid());
            }
        } else {
            // 当该评论是一级评论的时候，说明是对 博客详情、留言板、关于我
            // 判断是否开启邮件通知
            SystemConfig systemConfig = systemConfigService.getSystemConfig();
            if (systemConfig != null && EnumsStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
                if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                    log.info("发送评论邮件通知");
                    String sourceName = EnumCommentSource.valueOf(commentVO.getSource()).getName();
                    String linkText = "<a href=\" " + getUrlByCommentSource(commentVO) + "\">" + sourceName + "</a>\n";
                    String commentContent = linkText + "收到新的评论: " + commentVO.getContent();
                    // TODO: 2021/8/25 未完成rabbitMq
//                    rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), commentContent);
                } else {
                    log.error("网站没有配置通知接收的邮箱地址！");
                }
            }
        }
        comment.setUserUid(commentVO.getUserUid());
        comment.setToUid(commentVO.getToUid());
        comment.setStatus(EnumsStatus.ENABLE);
        this.save(comment);
        //获取图片
        if (StringUtils.isNotEmpty(user.getAvatar())) {
            List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(user.getAvatar(),
                    BaseSysConf.FILE_SEGMENTATION);
            if (webUtils.getPicture(picture).size() > 0) {
                user.setPhotoUrl(webUtils.getPicture(picture).get(0));
            }
        }
        comment.setUser(user);
        // 如果是回复某人的评论，那么需要向该用户Redis收件箱中中写入一条记录
        if (StringUtils.isNotEmpty(comment.getToUserUid())) {
            String redisKey =
                    BaseRedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + comment.getToUserUid();
            Object count = redisUtil.get(redisKey);
            if (StringUtils.isNotNull(count)) {
                redisUtil.increment(redisKey, Constants.NUM_ONE);
            } else {
                redisUtil.set(redisKey, 1, 86400 * 7);
            }
        }
        return ResultBody.success(comment);
    }

    @Override
    public void report(CommentParamVO commentVO) {
        // 获取举报的评论信息
        Comment comment = this.getById(commentVO.getUid());
        // 判断是否此评论存在
        if (StringUtils.isNull(comment) || EnumsStatus.DISABLED == comment.getStatus()) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.COMMENT_IS_NOT_EXIST);
        }
        // 判断举报的评论是否为当前用户的
        if (comment.getUserUid().equals(commentVO.getUserUid())) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.CAN_NOT_REPORT_YOURSELF_COMMENTS);
        }
        // 判断当前评论已经被举报过
        LambdaQueryWrapper<CommentReport> commentReportWrapper = new LambdaQueryWrapper<>();
        commentReportWrapper.eq(CommentReport::getUserUid, commentVO.getUserUid());
        commentReportWrapper.eq(CommentReport::getReportCommentUid, commentVO.getUid());
        commentReportWrapper.eq(CommentReport::getStatus, EnumsStatus.ENABLE);
        List<CommentReport> commentReports = commentReportService.list(commentReportWrapper);
        if (StringUtils.isNotEmpty(commentReports)) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.CAN_NOT_REPEAT_REPORT_COMMENT);
        }
        CommentReport commentReport = new CommentReport();
        commentReport.setContent(commentVO.getContent());
        commentReport.setProgress(0);
        // 从VO中获取举报的用户uid
        commentReport.setUserUid(commentVO.getUserUid());
        commentReport.setReportCommentUid(comment.getUid());
        // 从entity中获取被举报的用户uid
        commentReport.setReportUserUid(comment.getUserUid());
        commentReport.setStatus(EnumsStatus.ENABLE);
        commentReportService.save(commentReport);
    }

    @Override
    public void delete(CommentParamVO commentVO) {
        // 获取需要删除的评论信息
        Comment comment = this.getById(commentVO.getUid());
        // 判断当前评论是否存在
        if (StringUtils.isNull(comment) || EnumsStatus.DISABLED == comment.getStatus()) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.COMMENT_IS_NOT_EXIST);
        }
        // 判断此评论是否为自己的评论
        if (!comment.getUserUid().equals(commentVO.getUserUid())) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.DATA_NO_PRIVILEGE);
        }
        comment.setStatus(EnumsStatus.DISABLED);
        this.updateById(comment);
        // 获取该评论下的子评论进行删除
        // 传入需要被删除的评论 【因为这里是一条，我们需要用List包装一下，以后可以用于多评论的子评论删除】
        List<Comment> commentList = Collections.singletonList(comment);
        // 判断删除的是一级评论还是子评论
        String firstCommentUid = "";
        if (StringUtils.isNotEmpty(comment.getFirstCommentUid())) {
            // 删除的是子评论
            firstCommentUid = comment.getFirstCommentUid();
        } else {
            // 删除的是一级评论
            firstCommentUid = comment.getUid();
        }
        // 获取该评论下所有评论
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getFirstCommentUid, firstCommentUid);
        commentWrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
        List<Comment> comments = this.list(commentWrapper);
        List<Comment> resultList = new ArrayList<>();
        getToCommentList(comment, commentList, resultList);
        // 将所有的子评论也删除
        if (resultList.size() > 0) {
            resultList.forEach(item -> {
                item.setStatus(EnumsStatus.DISABLED);
                item.setUpdateTime(new Date());
            });
            this.updateBatchById(resultList);
        }
    }

    @Override
    public Integer getCommentCount(int enableFlag) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getStatus, enableFlag);
        return this.count(wrapper);
    }

    @Override
    public ResultBody addComment(CommentVO commentVO) {
        Comment comment = new Comment();
        comment.setSource(commentVO.getSource());
        comment.setBlogUid(commentVO.getBlogUid());
        comment.setContent(commentVO.getContent());
        comment.setUserUid(commentVO.getUserUid());
        comment.setToUid(commentVO.getToUid());
        comment.setToUserUid(commentVO.getToUserUid());
        comment.setStatus(EnumsStatus.ENABLE);
        comment.setUpdateTime(new Date());
        this.save(comment);
        return ResultBody.success();
    }

    @Override
    public ResultBody editComment(CommentVO commentVO) {
        Comment comment = this.getById(commentVO.getUid());
        if (StringUtils.isNull(comment)) {
            return ResultBody.error("当前评论已不存在");
        }
        comment.setSource(commentVO.getSource());
        comment.setBlogUid(commentVO.getBlogUid());
        comment.setContent(commentVO.getContent());
        comment.setUserUid(commentVO.getUserUid());
        comment.setToUid(commentVO.getToUid());
        comment.setToUserUid(commentVO.getToUserUid());
        comment.setStatus(EnumsStatus.ENABLE);
        comment.setUpdateTime(new Date());
        this.updateById(comment);
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteComment(CommentVO commentVO) {
        Comment comment = this.getById(commentVO.getUid());
        if (StringUtils.isNull(comment)) {
            return ResultBody.error("当前评论已不存在");
        }
        comment.setStatus(EnumsStatus.DISABLED);
        comment.setUpdateTime(new Date());
        this.updateById(comment);
        return ResultBody.success();
    }

    @Override
    public ResultBody deleteBatchComment(List<CommentVO> commentVOList) {
        List<String> uidList = new ArrayList<>();
        commentVOList.forEach(item -> {
            uidList.add(item.getUid());
        });
        List<Comment> commentList = this.listByIds(uidList);

        commentList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EnumsStatus.DISABLED);
        });
        this.updateBatchById(commentList);
        return ResultBody.success();
    }

    /**
     * 获取评论所有回复
     *
     * @param list             所有无回复评论
     * @param toCommentListMap 所有回复评论
     * @return 评论信息
     * @author yujunhong
     * @date 2021/8/25 14:28
     */
    private List<Comment> getCommentReply(List<Comment> list, Map<String, List<Comment>> toCommentListMap) {
        if (StringUtils.isEmpty(list)) {
            return new ArrayList<>();
        } else {
            list.forEach(item -> {
                String commentUid = item.getUid();
                List<Comment> replyCommentList = toCommentListMap.get(commentUid);
                List<Comment> replyComments = getCommentReply(replyCommentList, toCommentListMap);
                item.setReplyList(replyComments);
            });
            return list;
        }
    }

    /**
     * 通过评论类型跳转到对应的页面
     *
     * @param commentVO 评论实体对象
     * @return 跳转链接地址
     * @author yujunhong
     * @date 2021/8/25 16:40
     */
    private String getUrlByCommentSource(CommentParamVO commentVO) {
        String linkUrl = StringUtils.EMPTY;
        String commentSource = commentVO.getSource();
        switch (commentSource) {
            case "ABOUT": {
                linkUrl = dataWebsiteUrl + "about";
            }
            break;
            case "BLOG_INFO": {
                linkUrl = dataWebsiteUrl + "info?blogUid=" + commentVO.getBlogUid();
            }
            break;
            case "MESSAGE_BOARD": {
                linkUrl = dataWebsiteUrl + "messageBoard";
            }
            break;
            default: {
                linkUrl = dataWebsiteUrl;
                log.error("跳转到其它链接");
            }
        }
        return linkUrl;
    }

    /**
     * 获取某条评论下的所有子评论
     *
     * @param comment     当前评论
     * @param commentList 当前评论的子评论
     * @param resultList  子评论集合
     * @author yujunhong
     * @date 2021/8/26 14:45
     */
    private void getToCommentList(Comment comment, List<Comment> commentList, List<Comment> resultList) {
        if (comment == null) {
            return;
        }
        String commentUid = comment.getUid();
        for (Comment item : commentList) {
            if (commentUid.equals(item.getToUid())) {
                resultList.add(item);
                // 寻找子评论的子评论
                getToCommentList(item, commentList, resultList);
            }
        }
    }
}
