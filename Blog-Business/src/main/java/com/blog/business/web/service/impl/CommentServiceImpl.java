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
        // ??????????????????
        IPage<Comment> page = new Page<>();
        page.setCurrent(commentVO.getCurrentPage());
        page.setSize(commentVO.getPageSize());
        IPage<Comment> pageList = baseMapper.getPageList(page, commentVO);
        // ??????????????????
        List<Comment> commentList = pageList.getRecords();
        // ??????????????????????????????uid
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
        // ????????????
        List<Blog> blogList = new ArrayList<>();
        if (blogUidSet.size() > 0) {
            blogList = blogService.listByIds(blogUidSet);
        }
        Map<String, Blog> blogMap = new HashMap<>();
        blogList.forEach(item -> {
            // ???????????????????????????????????????????????????????????????
            item.setContent("");
            blogMap.put(item.getUid(), item);
        });
        // ????????????
        List<User> userCollection = new ArrayList<>();
        if (userUidSet.size() > 0) {
            userCollection = userService.listByIds(userUidSet);
        }
        // ????????????
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
            // ????????????????????????
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
                log.error("ECommentSource ????????????");
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
        // ?????????????????????????????????
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(commentParamVO.getBlogUid())) {
            wrapper.eq(Comment::getBlogUid, commentParamVO.getBlogUid());
        }
        Page<Comment> page = new Page<>();
        page.setCurrent(commentParamVO.getCurrentPage());
        page.setSize(commentParamVO.getPageSize());
        wrapper.eq(Comment::getSource, commentParamVO.getSource());
        wrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
        // ???????????????????????????
        wrapper.isNull(Comment::getToUid);
        wrapper.eq(Comment::getType, EnumsStatus.COMMENT);
        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> commentPage = this.page(page, wrapper);
        List<Comment> comments = commentPage.getRecords();
        // ?????????????????????uid-???????????????
        List<String> uidList = comments.stream().map(Comment::getUid).collect(Collectors.toList());
        // ????????????????????????
        if (StringUtils.isNotEmpty(uidList)) {
            wrapper.clear();
            wrapper.in(Comment::getFirstCommentUid, uidList);
            wrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
            List<Comment> notFirstCommentList = this.list(wrapper);
            comments.addAll(notFirstCommentList);
        }
        // ???????????????????????????
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
        // ??????????????????
        List<User> userList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userUidList)) {
            userList = userService.getUserListByIds(userUidList);
        }
        // ??????????????????
        StringBuilder avatarString = new StringBuilder();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                avatarString.append(item.getAvatar()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> pictureList = pictureFeignClient.getPicture(avatarString.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        List<Map<String, Object>> realPictureList = webUtils.getPictureMap(pictureList);
        // ????????????
        Map<String, String> pictureMap = new HashMap<>();
        realPictureList.forEach(item -> pictureMap.put(item.get(BaseSQLConf.UID).toString(),
                item.get(BaseSQLConf.URL).toString()));

        // ???????????????????????????????????????
        Map<String, User> userMap = new HashMap<>();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && StringUtils.isNotNull(pictureMap.get(item.getAvatar()))) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });
        // ?????????????????????????????????????????????
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
        // ?????????toUid???????????????
        List<Comment> toUidCommentList =
                comments.stream().filter(item -> StringUtils.isNotEmpty(item.getToUid())).collect(Collectors.toList());
        // ????????????toUid????????????
        Map<String, List<Comment>> commentToUidMap =
                toUidCommentList.stream().collect(Collectors.groupingBy(Comment::getToUid));
        // ??????toUid???????????????--???????????????
        List<Comment> firstCommentList =
                comments.stream().filter(item -> StringUtils.isEmpty(item.getToUid())).collect(Collectors.toList());
        List<Comment> commentReply = getCommentReply(firstCommentList, commentToUidMap);
        commentPage.setRecords(commentReply);
        return commentPage;
    }

    @Override
    public Map<String, Object> getListByUser(UserVO userVO, String userUid) {
        // ????????????
        Page<Comment> page = new Page<>();
        page.setCurrent(userVO.getCurrentPage());
        page.setSize(userVO.getPageSize());
        // ???????????????????????????????????????
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getType, EnumsStatus.COMMENT);
        commentWrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
        commentWrapper.and(wrapper -> wrapper.eq(Comment::getUserUid, userUid).or().eq(Comment::getToUserUid, userUid));
        commentWrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> commentPage = this.page(page, commentWrapper);
        List<Comment> commentList = commentPage.getRecords();
        // ?????????????????????id??????????????????id
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
        // ??????????????????
        List<User> userList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userIdList)) {
            userList = userService.getUserListByIds(userIdList);
        }
        // ??????????????????
        StringBuilder avatarString = new StringBuilder();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                avatarString.append(item.getAvatar()).append(BaseSysConf.FILE_SEGMENTATION);
            }
        });
        List<Map<String, Object>> pictureList = pictureFeignClient.getPicture(avatarString.toString(),
                BaseSysConf.FILE_SEGMENTATION);
        pictureList = webUtils.getPictureMap(pictureList);
        // ????????????
        Map<String, String> pictureMap = new HashMap<>();
        pictureList.forEach(item -> pictureMap.put(item.get(BaseSQLConf.UID).toString(),
                item.get(BaseSQLConf.URL).toString()));
        // ???????????????????????????????????????
        Map<String, User> userMap = new HashMap<>();
        userList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && StringUtils.isNotNull(pictureMap.get(item.getAvatar()))) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });
        // ???????????????????????? ???????????? ??? ????????????
        List<Comment> userCommentList = new ArrayList<>();
        List<Comment> replyList = new ArrayList<>();
        commentList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }

            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }
            // ??????sourceName
            if (StringUtils.isNotEmpty(item.getSource())) {
                try {
                    item.setSourceName(EnumCommentSource.valueOf(item.getSource()).getName());
                } catch (Exception e) {
                    log.error("EnumCommentSource????????????");
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
        // ????????????
        Page<Comment> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(currentPage);
        // ??????????????????
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getUserUid, userUid);
        commentWrapper.eq(Comment::getType, EnumsStatus.PRAISE);
        commentWrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
        commentWrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = this.page(page, commentWrapper);
        List<Comment> commentList = commentPage.getRecords();
        // ???????????????????????????
        List<String> blogUidList = commentList.stream().map(Comment::getBlogUid).collect(Collectors.toList());
        // ??????????????????
        Map<String, Blog> blogMap = new HashMap<>();
        if (blogUidList.size() > 0) {
            List<Blog> blogList = blogService.listByIds(blogUidList);
            blogList.forEach(blog -> {
                // ????????????content??????
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
        // ????????????????????????
        LambdaQueryWrapper<WebConfig> webConfigWrapper = new LambdaQueryWrapper<>();
        webConfigWrapper.eq(WebConfig::getStatus, EnumsStatus.ENABLE);
        WebConfig webConfig = webConfigService.getOne(webConfigWrapper);
        // ????????????????????????????????????
        if (BaseSysConf.CAN_NOT_COMMENT.equals(webConfig.getOpenComment())) {
            return ResultBody.error(BaseMessageConf.NO_COMMENTS_OPEN);
        }
        // ??????????????????????????????????????????
        if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
            Blog blog = blogService.getById(commentVO.getBlogUid());
            if (BaseSysConf.CAN_NOT_COMMENT.equals(blog.getOpenComment())) {
                return ResultBody.error(BaseMessageConf.NO_COMMENTS_OPEN);
            }
        }
        // ??????????????????
        User user = userService.getById(userUid);
        // ??????????????????????????????
        if (commentVO.getContent().length() > BaseSysConf.ONE_ZERO_TWO_FOUR) {
            return ResultBody.error(BaseMessageConf.COMMENT_CAN_NOT_MORE_THAN_1024);
        }
        // ??????????????????????????????
        if (user.getCommentStatus() == BaseSysConf.ZERO) {
            return ResultBody.error(BaseMessageConf.YOU_DONT_HAVE_PERMISSION_TO_SPEAK);
        }
        // ???????????????????????????????????????
        Object jsonResult =
                redisUtil.get(BaseRedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid);
        if (StringUtils.isNotNull(jsonResult)) {
            int count = Integer.parseInt(String.valueOf(jsonResult));
            if (count >= Constants.NUM_FIVE) {
                return ResultBody.error(BaseMessageConf.PLEASE_TRY_AGAIN_IN_AN_HOUR);
            }
        }
        // ????????????????????????
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
                    // ??????????????????
                    rabbitMqUtils.sendCommentEmail(map);
                }

            }
        }
        Comment comment = new Comment();
        comment.setSource(commentVO.getSource());
        comment.setBlogUid(commentVO.getBlogUid());
        comment.setContent(commentVO.getContent());
        comment.setToUserUid(commentVO.getToUserUid());

        // ????????????????????????????????????????????????????????????UID??????
        if (StringUtils.isNotEmpty(commentVO.getToUid())) {
            Comment toComment = this.getById(commentVO.getToUid());
            // ?????? toComment??????????????????
            if (toComment != null && StringUtils.isNotEmpty(toComment.getFirstCommentUid())) {
                comment.setFirstCommentUid(toComment.getFirstCommentUid());
            } else {
                // ?????????????????????????????????????????????UID
                comment.setFirstCommentUid(toComment.getUid());
            }
        } else {
            // ??????????????????????????????????????????????????? ????????????????????????????????????
            // ??????????????????????????????
            SystemConfig systemConfig = systemConfigService.getSystemConfig();
            if (systemConfig != null && EnumsStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
                if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                    log.info("????????????????????????");
                    String sourceName = EnumCommentSource.valueOf(commentVO.getSource()).getName();
                    String linkText = "<a href=\" " + getUrlByCommentSource(commentVO) + "\">" + sourceName + "</a>\n";
                    String commentContent = linkText + "??????????????????: " + commentVO.getContent();
                    // TODO: 2021/8/25 ?????????rabbitMq
//                    rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), commentContent);
                } else {
                    log.error("????????????????????????????????????????????????");
                }
            }
        }
        comment.setUserUid(commentVO.getUserUid());
        comment.setToUid(commentVO.getToUid());
        comment.setStatus(EnumsStatus.ENABLE);
        this.save(comment);
        //????????????
        if (StringUtils.isNotEmpty(user.getAvatar())) {
            List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(user.getAvatar(),
                    BaseSysConf.FILE_SEGMENTATION);
            if (webUtils.getPicture(picture).size() > 0) {
                user.setPhotoUrl(webUtils.getPicture(picture).get(0));
            }
        }
        comment.setUser(user);
        // ?????????????????????????????????????????????????????????Redis?????????????????????????????????
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
        // ???????????????????????????
        Comment comment = this.getById(commentVO.getUid());
        // ???????????????????????????
        if (StringUtils.isNull(comment) || EnumsStatus.DISABLED == comment.getStatus()) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.COMMENT_IS_NOT_EXIST);
        }
        // ?????????????????????????????????????????????
        if (comment.getUserUid().equals(commentVO.getUserUid())) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.CAN_NOT_REPORT_YOURSELF_COMMENTS);
        }
        // ????????????????????????????????????
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
        // ???VO????????????????????????uid
        commentReport.setUserUid(commentVO.getUserUid());
        commentReport.setReportCommentUid(comment.getUid());
        // ???entity???????????????????????????uid
        commentReport.setReportUserUid(comment.getUserUid());
        commentReport.setStatus(EnumsStatus.ENABLE);
        commentReportService.save(commentReport);
    }

    @Override
    public void delete(CommentParamVO commentVO) {
        // ?????????????????????????????????
        Comment comment = this.getById(commentVO.getUid());
        // ??????????????????????????????
        if (StringUtils.isNull(comment) || EnumsStatus.DISABLED == comment.getStatus()) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.COMMENT_IS_NOT_EXIST);
        }
        // ???????????????????????????????????????
        if (!comment.getUserUid().equals(commentVO.getUserUid())) {
            throw new CommonErrorException(BaseSysConf.ERROR, BaseMessageConf.DATA_NO_PRIVILEGE);
        }
        comment.setStatus(EnumsStatus.DISABLED);
        this.updateById(comment);
        // ??????????????????????????????????????????
        // ?????????????????????????????? ??????????????????????????????????????????List???????????????????????????????????????????????????????????????
        List<Comment> commentList = Collections.singletonList(comment);
        // ?????????????????????????????????????????????
        String firstCommentUid = "";
        if (StringUtils.isNotEmpty(comment.getFirstCommentUid())) {
            // ?????????????????????
            firstCommentUid = comment.getFirstCommentUid();
        } else {
            // ????????????????????????
            firstCommentUid = comment.getUid();
        }
        // ??????????????????????????????
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getFirstCommentUid, firstCommentUid);
        commentWrapper.eq(Comment::getStatus, EnumsStatus.ENABLE);
        List<Comment> comments = this.list(commentWrapper);
        List<Comment> resultList = new ArrayList<>();
        getToCommentList(comment, commentList, resultList);
        // ??????????????????????????????
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
            return ResultBody.error("????????????????????????");
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
            return ResultBody.error("????????????????????????");
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

    @Override
    public void batchDeleteCommentByBlogUid(List<String> blogUid) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Comment::getBlogUid, blogUid);
        List<Comment> commentList = this.list(wrapper);
        commentList.forEach(item -> item.setStatus(EnumsStatus.DISABLED));
        this.updateBatchById(commentList);
    }

    /**
     * ????????????????????????
     *
     * @param list             ?????????????????????
     * @param toCommentListMap ??????????????????
     * @return ????????????
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
     * ??????????????????????????????????????????
     *
     * @param commentVO ??????????????????
     * @return ??????????????????
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
                log.error("?????????????????????");
            }
        }
        return linkUrl;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param comment     ????????????
     * @param commentList ????????????????????????
     * @param resultList  ???????????????
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
                // ???????????????????????????
                getToCommentList(item, commentList, resultList);
            }
        }
    }
}
