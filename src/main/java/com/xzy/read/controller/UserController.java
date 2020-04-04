package com.xzy.read.controller;

import com.alibaba.fastjson.JSONObject;
import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.User;
import com.xzy.read.service.*;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author XieZhongYi
 * 2020/03/23 17:04
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    private TopicService topicService;

    private NoteBooksService noteBooksService;

    private ArticleService articleService;

    private FollowService followService;

    @Autowired
    private SmsUtil smsUtil;

    public UserController(UserService userService, TopicService topicService, NoteBooksService noteBooksService, ArticleService articleService, FollowService followService) {
        this.userService = userService;
        this.topicService = topicService;
        this.noteBooksService = noteBooksService;
        this.articleService = articleService;
        this.followService = followService;
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

    @GetMapping("/info/{id}")
    public ResultVo userInfoById(@PathVariable Long id) {
        return userService.basicUserInfo(id);
    }

    @GetMapping("/info/topic/{id}")
    public ResultVo userInfoTopicById(@PathVariable Long id) {
        return ResultVoUtil.success(topicService.getAllTopicsByUserId(id));
    }

    @GetMapping("/info/nb/{id}")
    public ResultVo userInfoNbById(@PathVariable Long id) {
        return ResultVoUtil.success(noteBooksService.findAllByUserId(id));
    }

    @GetMapping("/{id}/like/articles")
    public ResultVo userLikeArticles(@PathVariable Long id,@RequestParam(defaultValue = "1") int page) {
        return articleService.likeArticles(id,page);
    }

    @GetMapping("/{id}/follows")
    public ResultVo userFollows(@PathVariable Long id,@RequestParam(defaultValue = "1") int page) {
        return followService.findAllTopicsByUserId(id,page);
    }

    @PostMapping("head")
    public ResultVo updateHead(@RequestParam("file") MultipartFile multipartFile) {
        return userService.uploadHead(multipartFile);
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
