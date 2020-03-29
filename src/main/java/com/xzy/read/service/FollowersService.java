package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Followers;

/**
 * @author XieZhongYi
 * 2020/03/28 20:44
 */
public interface FollowersService {

    ResultVo follow(Followers followers);

    Long countfollowers(Long fromUserId);

    Long countFans(Long toUserId);

    Long countByFromUserIdAndToUserIdAndStatus(Long fromUserId, Long toUserId, Boolean status);


}
