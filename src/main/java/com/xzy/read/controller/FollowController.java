package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Followers;
import com.xzy.read.service.FollowersService;
import org.springframework.web.bind.annotation.*;

/**
 * @author XieZhongYi
 * 2020/03/28 21:00
 */
@RestController
@RequestMapping("/api")
public class FollowController {

    private FollowersService followersService;

    public FollowController(FollowersService followersService) {
        this.followersService = followersService;
    }


    @PutMapping("/follow")
    public ResultVo follow(@RequestBody Followers followers) {
        return followersService.follow(followers);
    }

    @GetMapping("/follow/followers")
    public Long followers(Long id) {
        return followersService.countfollowers(id);
    }

    @GetMapping("/follow/fans")
    public Long fans(Long id) {
        return followersService.countFans(id);
    }

}
