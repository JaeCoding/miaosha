package com.imooc.miaosha.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);//导入slf4j的Logger

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }


    /**
     * 1.校验功能不用放在Controller中，而是放在Service中
     * 2.传入参数的校验：如是否为空，手机号是否是以1开头，是否是11位（虽然在前端就做了限制位数）这类初级校验交给@Valid
     * 3.给LoginVo类的各种属性加上所需注解，可以自定义注解并validated实现
     * 4.Service中做业务的校验，如手机是否存在，密码是否正确。不满足抛出异常
     * 5.不满足自定义注解的异常，
     * @param response
     * @param loginVo
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());

        miaoshaUserService.login(response, loginVo); //失败了有异常处理

        //跳转到list界面
        return Result.success(true);


    }

}
