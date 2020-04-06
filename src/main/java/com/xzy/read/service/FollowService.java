package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Followers;
import com.xzy.read.entity.Follows;
import com.xzy.read.entity.enums.FollowType;

/**
 * @author XieZhongYi
 * 2020/03/28 20:44
 */
public interface FollowService {

    ResultVo follow(Followers followers);

    Long countfollowers(Long fromUserId);

    Long countFans(Long toUserId);

    Long countByFromUserIdAndToUserIdAndStatus(Long fromUserId, Long toUserId, Boolean status);

    ResultVo findAllFansByUserId(Long userId, int page);

    ResultVo findAllFollowersByUserId(Long userId, int page);

    ResultVo follow(Follows follows, FollowType followType);

    Long countFansByType(Long typeId, FollowType followType);

    ResultVo followerByType(int page, int size, Long typeId,FollowType followType);

    ResultVo findAllTopicsByUserId(Long userId, int page);

    ResultVo findArticlesByTimeline(Long userId, int page);

    ResultVo findAllFollowsByUserId(Long userId, String type);

    ResultVo findAllFollowersByUserId(Long userId);

}
