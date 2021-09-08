package com.cloud.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.business.admin.domain.SystemConfig;
import com.blog.business.admin.service.SystemConfigService;
import com.blog.business.utils.WebUtils;
import com.blog.business.web.domain.User;
import com.blog.business.web.service.FeedbackService;
import com.blog.business.web.service.LinkService;
import com.blog.business.web.service.UserService;
import com.blog.business.web.service.WebConfigService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.*;
import com.blog.entity.FileVO;
import com.blog.exception.ResultBody;
import com.blog.feign.PictureFeignClient;
import com.blog.utils.Md5Utils;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yujunhong
 * @date 2021/9/6 15:39
 */
@RestController
@RequestMapping(value = "/oauth")
@Api(value = "第三方登录相关接口", tags = {"第三方登录相关接口"})
@Slf4j
public class AuthController {
    @Autowired
    private WebUtils webUtils;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private UserService userService;
    @Value(value = "${justAuth.clientId.github}")
    private String githubClientId;
    @Value(value = "${justAuth.clientSecret.github}")
    private String githubClientSecret;
    @Value(value = "${justAuth.clientId.gitee}")
    private String giteeClientId;
    @Value(value = "${justAuth.clientSecret.gitee}")
    private String giteeClientSecret;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PictureFeignClient pictureFeignClient;
    @Value(value = "${data.web.url}")
    private String blogUrl;
    @Value(value = "${data.webSite.url}")
    private String webSiteUrl;
    @Value(value = "${data.web.project_name_en}")
    private String projectNameEn;
    @Value(value = "${BLOG.DEFAULT_PWD}")
    private String defaultPwd;
    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
    private Long userTokenSurvivalTime;

    /**
     * 获取认证
     *
     * @param source 使用哪一种方式进行登录
     * @return 验证信息
     * @author yujunhong
     * @date 2021/9/6 15:51
     */
    @PostMapping(value = "/render")
    @ApiOperation(value = "获取认证")
    public ResultBody renderAuth(String source) {
        // 将传递过来的转换成大写
        Boolean isOpenLoginType = webConfigService.isOpenLoginType(source.toUpperCase());
        if (!isOpenLoginType) {
            return ResultBody.error(BaseSysConf.ERROR, "后台未开启该登录方式!");
        }
        log.info("进入render:" + source);
        AuthRequest authRequest = getAuthRequest(source);
        String token = AuthStateUtils.createState();
        String authorizeUrl = authRequest.authorize(token);
        Map<String, String> map = new HashMap<>();
        map.put(BaseSQLConf.URL, authorizeUrl);
        return ResultBody.success(map);
    }

    /**
     * oauth平台中配置的授权回调地址
     *
     * @param source              登录类型
     * @param callback            回调函数实体
     * @param httpServletResponse 响应实体
     * @author yujunhong
     * @date 2021/9/6 16:14
     */
    @ApiOperation(value = "oauth平台中配置的授权回调地址")
    @RequestMapping(value = "/callback/{source}")
    public void login(@PathVariable(value = "source") String source, AuthCallback callback,
                      HttpServletResponse httpServletResponse) throws IOException {
        log.info("进入callback：" + source + " callback params：" + JSONObject.toJSONString(callback));
        AuthRequest authRequest = getAuthRequest(source);
        AuthResponse response = authRequest.login(callback);
        if (response.getCode() == Constants.NUM_5000) {
            // 跳转到500错误页面
            httpServletResponse.sendRedirect(webSiteUrl + Constants.STR_500);
            return;
        }
        String result = JSONObject.toJSONString(response);
        Map<String, Object> map = JSON.parseObject(result, new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> data = JSON.parseObject(JSON.toJSONString(map.get(BaseSysConf.DATA)),
                new TypeReference<Map<String, Object>>() {
                });
        Map<String, Object> token = new HashMap<>();
        String accessToken = "";
        if (data == null || data.get(BaseSysConf.TOKEN) == null) {
            // 跳转到500错误页面
            httpServletResponse.sendRedirect(webSiteUrl + Constants.STR_500);
            return;
        } else {
            token = JSON.parseObject(JSON.toJSONString(data.get(BaseSysConf.TOKEN)),
                    new TypeReference<Map<String, Object>>() {
                    });
            ;
            accessToken = token.get(BaseSysConf.ACCESS_TOKEN).toString();
        }
        boolean exist = false;
        User user;
        //判断user是否存在
        if (data.get(BaseSysConf.UUID) != null && data.get(BaseSysConf.SOURCE) != null) {
            user = userService.getUserBySourceAndUuid(data.get(BaseSysConf.SOURCE).toString(),
                    data.get(BaseSysConf.UUID).toString());
            if (user != null) {
                exist = true;
            } else {
                user = new User();
            }
        } else {
            return;
        }
        // 判断邮箱是否存在
        if (data.get(BaseSysConf.EMAIL) != null) {
            String email = data.get(BaseSysConf.EMAIL).toString();
            user.setEmail(email);
        }

        // 判断用户性别
        if (data.get(BaseSysConf.GENDER) != null && !exist) {
            String gender = data.get(BaseSysConf.GENDER).toString();
            if (BaseSysConf.MALE.equals(gender)) {
                user.setGender(Constants.MALE);
            } else if (BaseSysConf.FEMALE.equals(gender)) {
                user.setGender(Constants.FEMALE);
            } else {
                user.setGender(Constants.UNKNOWN);
            }
        }
        // 通过头像uid获取图片
        List<Map<String, Object>> picture = this.pictureFeignClient.getPicture(user.getAvatar(),
                BaseSysConf.FILE_SEGMENTATION);
        // 判断该用户是否含有头像信息
        if (StringUtils.isNotEmpty(picture)) {
            String fileOldName = picture.get(0).get(BaseSysConf.FILE_OLD_NAME).toString();
            // 判断本地的图片是否和第三方登录的一样，如果不一样，那么更新
            // 如果旧名称为blob表示是用户自定义的，代表用户在本网站使用了自定义头像，那么就再也不同步更新网站上的了
            if (fileOldName.equals(data.get(BaseSysConf.AVATAR)) || BaseSysConf.BLOB.equals(fileOldName)) {
                user.setPhotoUrl((String) picture.get(0).get(BaseSysConf.URL));
            } else {
                updateUserPhoto(data, user);
            }
        }
        if (data.get(BaseSysConf.NICKNAME) != null) {
            user.setNickName(data.get(BaseSysConf.NICKNAME).toString());
        }

        if (user.getLoginCount() == null) {
            user.setLoginCount(0);
        } else {
            user.setLoginCount(user.getLoginCount() + 1);
        }
        // 获取浏览器，IP来源，以及操作系统
        user = userService.setRequestInfo(user);
        // 暂时将token也存入到user表中，为了以后方便更新redis中的内容
        user.setValidCode(accessToken);
        if (exist) {
            userService.updateById(user);
        } else {
            user.setUuid(data.get(BaseSysConf.UUID).toString());
            user.setSource(data.get(BaseSysConf.SOURCE).toString());
            String userName =
                    projectNameEn.concat(Constants.SYMBOL_UNDERLINE).concat(user.getSource()).concat(Constants.SYMBOL_UNDERLINE).concat(user.getUuid());
            user.setUserName(userName);
            // 如果昵称为空，那么直接设置用户名
            if (StringUtils.isEmpty(user.getNickName())) {
                user.setNickName(userName);
            }
            // 默认密码
            user.setPassWord(Md5Utils.stringToMd5(defaultPwd));
            // 设置是否开启评论邮件通知【关闭】
            user.setStartEmailNotification(EnumsStatus.CLOSE_STATUS);
            userService.save(user);
        }
        // 过滤密码
        user.setPassWord("");
        //将从数据库查询的数据缓存到redis中
        redisUtil.set(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken, JSON.toJSONString(user),
                userTokenSurvivalTime * 60 * 60);

        httpServletResponse.sendRedirect(webSiteUrl + "?token=" + accessToken);
    }

    /**
     * 刷新token
     *
     * @param source 第三方授权平台
     * @param token  login成功后返回的refreshToken
     * @author yujunhong
     * @date 2021/9/6 16:14
     */
    @RequestMapping("/refresh/{source}")
    public Object refreshAuth(@PathVariable("source") String source, String token) {
        AuthRequest authRequest = getAuthRequest(source);
        return authRequest.refresh(AuthToken.builder().refreshToken(token).build());
    }

    /**
     * 取消授权
     *
     * @param source 第三方授权平台
     * @param token  login成功后返回的accessToken
     * @author yujunhong
     * @date 2021/9/6 16:14
     */
    @RequestMapping("/revoke/{source}/{token}")
    public Object revokeAuth(@PathVariable("source") String source, @PathVariable("token") String token) throws IOException {
        AuthRequest authRequest = getAuthRequest(source);
        return authRequest.revoke(AuthToken.builder().accessToken(token).build());
    }

    /**
     * 获取用户信息
     *
     * @param accessToken 鉴权token
     * @return ResultBody
     * @author yujunhong
     * @date 2021/9/7 16:42
     */
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @GetMapping("/verify/{accessToken}")
    public ResultBody verifyUser(@PathVariable("accessToken") String accessToken) {
        String userInfo = (String) redisUtil.get(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken);
        if (StringUtils.isEmpty(userInfo)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.INVALID_TOKEN);
        } else {
            Map<String, Object> map = JSON.parseObject(userInfo, new TypeReference<Map<String, Object>>() {
            });
            return ResultBody.success(map);
        }
    }

    /**
     * 鉴权
     *
     * @param source 登陆方式
     * @return AuthRequest
     * @author yujunhong
     * @date 2021/9/6 16:09
     */
    private AuthRequest getAuthRequest(String source) {
        AuthRequest authRequest = null;
        switch (source) {
            case BaseSysConf.GITHUB:
                authRequest = new AuthGithubRequest(AuthConfig.builder()
                        .clientId(githubClientId)
                        .clientSecret(githubClientSecret)
                        .redirectUri(blogUrl + "/oauth/callback/github")
                        .build());
                break;
                case BaseSysConf.GITEE:
                authRequest = new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(giteeClientId)
                        .clientSecret(giteeClientSecret)
                        .redirectUri(blogUrl + "/oauth/callback/gitee")
                        .build());
                break;

            default:
                break;
        }
        if (null == authRequest) {
            throw new AuthException(BaseMessageConf.OPERATION_FAIL);
        }
        return authRequest;
    }

    /**
     * 更新用户头像
     *
     * @param data 第三方返回用户信息
     * @param user 用户信息
     * @author yujunhong
     * @date 2021/9/7 15:27
     */
    private void updateUserPhoto(Map<String, Object> data, User user) {
        // 获取配置信息
        LambdaQueryWrapper<SystemConfig> systemConfigWrapper = new LambdaQueryWrapper<>();
        systemConfigWrapper.eq(SystemConfig::getStatus, EnumsStatus.ENABLE);
        SystemConfig systemConfig = systemConfigService.getOne(systemConfigWrapper);
        if (StringUtils.isNull(systemConfig)) {
            return;
        }
        // 获取到头像，然后上传到自己服务器
        FileVO fileVO = new FileVO();
        fileVO.setAdminUid(BaseSysConf.DEFAULT_UID);
        fileVO.setUserUid(BaseSysConf.DEFAULT_UID);
        fileVO.setProjectName(BaseSysConf.BLOG);
        fileVO.setSortName(BaseSysConf.ADMIN);
        fileVO.setSystemConfig(JSON.parseObject(JSON.toJSONString(systemConfig), new TypeReference<Map<String,
                String>>() {
        }));
        List<String> urlList = new ArrayList<>();
        if (data.get(BaseSysConf.AVATAR) != null) {
            urlList.add(data.get(BaseSysConf.AVATAR).toString());
        } else if (data.get(BaseSysConf.AVATAR_URL) != null) {
            urlList.add(data.get(BaseSysConf.AVATAR_URL).toString());
        }
        fileVO.setUrlList(urlList);
        // 上传图片信息
        String result = (String) this.pictureFeignClient.uploadPictureByUrl(fileVO).getResult();
        JSONArray objects = JSON.parseArray(result);
        if (StringUtils.isNotEmpty(objects)) {
            Map<String, Object> pictureMap = JSON.parseObject(JSON.toJSONString(objects.get(0)),
                    new TypeReference<Map<String, Object>>() {
                    });

            String localPictureBaseUrl = systemConfig.getLocalPictureBaseUrl();
            String qiNiuPictureBaseUrl = systemConfig.getQiNiuPictureBaseUrl();
            String picturePriority = systemConfig.getPicturePriority();
            user.setAvatar(pictureMap.get(BaseSysConf.UID).toString());
            // 判断图片优先展示
            if (EnumsStatus.OPEN.equals(picturePriority)) {
                // 使用七牛云
                if (pictureMap.get(BaseSysConf.QI_NIU_URL) != null && pictureMap.get(BaseSysConf.UID) != null) {
                    user.setPhotoUrl(qiNiuPictureBaseUrl + pictureMap.get(BaseSysConf.QI_NIU_URL).toString());
                }
            } else {
                // 使用自建图片服务器
                if (pictureMap.get(BaseSysConf.PIC_URL) != null && pictureMap.get(BaseSysConf.UID) != null) {
                    user.setPhotoUrl(localPictureBaseUrl + pictureMap.get(BaseSysConf.PIC_URL).toString());
                }
            }

        }
    }
}
