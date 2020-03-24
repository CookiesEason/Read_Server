package com.xzy.read.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.User;
import com.xzy.read.service.UserService;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XieZhongYi
 * 2020/03/23 17:04
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    @Autowired
    private SmsUtil smsUtil;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/relogin")
    public JSONObject login(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",301);
        jsonObject.put("msg","您还未登陆");
        return jsonObject;
    }

    @PostMapping("/register")
    public ResultVo registerUser(@RequestBody User user){
        return userService.save(user);
    }

    @GetMapping("/info")
    public ResultVo userInfo() {
        return userService.getUserInfo();
    }

    @PutMapping("/info")
    public ResultVo updateUser(@RequestBody User user){
        return userService.update(user);
    }

    @PutMapping("/resetPassword")
    public ResultVo resetPassword(@RequestBody User user){
        return userService.resetPassword(user);
    }

    @PutMapping("/resetTelephone")
    public ResultVo resetTelephone(@RequestBody User user){
        return userService.resetTelephone(user);
    }

    @GetMapping("/code")
    public ResultVo sendCode(String telephone) {
        return smsUtil.txSmsSend(telephone);
    }
}
