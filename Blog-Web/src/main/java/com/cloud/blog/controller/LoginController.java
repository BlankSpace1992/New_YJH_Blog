package com.cloud.blog.controller;

import com.blog.business.web.domain.vo.UserVO;
import com.blog.business.web.service.UserService;
import com.blog.business.web.service.WebConfigService;
import com.blog.config.redis.RedisUtil;
import com.blog.constants.BaseMessageConf;
import com.blog.constants.BaseRedisConf;
import com.blog.constants.BaseSysConf;
import com.blog.constants.Constants;
import com.blog.exception.ResultBody;
import com.blog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yujunhong
 * @date 2021/9/14 14:48
 */
@RestController
@RequestMapping(value = "/login")
@Api(value = "登录管理相关接口", tags = "登录管理相关接口")
public class LoginController {
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户登录
     *
     * @param userVO 登录实体对象
     * @return 成功/失败
     * @author yujunhong
     * @date 2021/9/14 15:08
     */
    @ApiOperation(value = "用户登录")
    @PostMapping(value = "/login")
    public ResultBody login(@RequestBody UserVO userVO) {
        Boolean openLoginType = webConfigService.isOpenLoginType(BaseRedisConf.PASSWORD);
        if (!openLoginType) {
            return ResultBody.error(BaseSysConf.ERROR, "后台未开启该登录方式!");
        }
        return userService.login(userVO);
    }

    /**
     * 用户注册
     *
     * @param userVO 注册实体对象
     * @return 成功/失败
     * @author yujunhong
     * @date 2021/9/14 15:59
     */
    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    public ResultBody register(@RequestBody UserVO userVO) {
        Boolean openLoginType = webConfigService.isOpenLoginType(BaseRedisConf.PASSWORD);
        if (!openLoginType) {
            return ResultBody.error("后台未开启注册功能!");
        }
        // 用户名满足范围,密码满足范围
        if (userVO.getUserName().length() < Constants.NUM_FIVE || userVO.getUserName().length() >= Constants.NUM_TWENTY || userVO.getPassWord().length() < Constants.NUM_FIVE || userVO.getPassWord().length() >= Constants.NUM_TWENTY) {
            return ResultBody.error(BaseMessageConf.PARAM_INCORRECT);
        }
        return userService.register(userVO);
    }

    /**
     * 激活用户
     *
     * @param token token值
     * @return 成功/失败
     * @author yujunhong
     * @date 2021/9/14 16:17
     */
    @GetMapping("/activeUser/{token}")
    @ApiOperation(value = "激活用户账号", notes = "激活用户账号")
    public ResultBody activeUser(@PathVariable(value = "token") String token) {
        return userService.activeUser(token);
    }

    /**
     * 退出登录
     *
     * @param token token值
     * @return 成功/失败
     * @author yujunhong
     * @date 2021/9/14 16:44
     */
    @ApiOperation(value = "退出登录", notes = "退出登录")
    @PostMapping(value = "/logout")
    public ResultBody logout(@ApiParam(name = "token", value = "token令牌", required = false) @RequestParam(name =
            "token", required = false) String token) {
        if (StringUtils.isEmpty(token)) {
            return ResultBody.error(BaseSysConf.ERROR, BaseMessageConf.OPERATION_FAIL);
        }
        redisUtil.set(BaseRedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token, "");
        return ResultBody.success();
    }
}
