package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XieZhongYi
 * 2020/04/01 10:14
 */
@RestController
@RequestMapping("/api/recommendUsers")
public class AuthorController {

    private UserService userService;


    public AuthorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResultVo findRecommendUsers(@RequestParam(defaultValue = "5") int size,@RequestParam(defaultValue = "1") int page) {
        return userService.recommendUsers(size, page-1);
    }
}
