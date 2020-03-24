package com.xzy.read.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.User;
import com.xzy.read.service.UserService;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @author XieZhongYi
 * 2020/03/23 17:04
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

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
        return ResultVoUtil.success("用户才能看见的");
    }

    @PutMapping("/info")
    public ResultVo updateUser(@RequestBody User user){
        return userService.update(user);
    }

}
