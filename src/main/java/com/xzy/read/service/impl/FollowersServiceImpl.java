package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Followers;
import com.xzy.read.repository.FollowersRepository;
import com.xzy.read.service.FollowersService;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.stereotype.Service;

/**
 * @author XieZhongYi
 * 2020/03/28 20:45
 */
@Service
public class FollowersServiceImpl implements FollowersService {

    private FollowersRepository followersRepository;

    public FollowersServiceImpl(FollowersRepository followersRepository) {
        this.followersRepository = followersRepository;
    }

    @Override
    public ResultVo follow(Followers followers) {
        Followers old = followersRepository.findByFromUserId(followers.getFromUserId());
        if (old != null) {
            old.setStatus(!old.getStatus());
            followersRepository.save(old);
            return ResultVoUtil.success();
        }
        followers.setStatus(true);
        followersRepository.save(followers);
        return ResultVoUtil.success();
    }

    @Override
    public Long countfollowers(Long fromUserId) {
        return followersRepository.countAllByFromUserIdAndStatus(fromUserId, true);
    }

    @Override
    public Long countFans(Long toUserId) {
        return followersRepository.countAllByToUserIdAndStatus(toUserId, true);
    }

    @Override
    public Long countByFromUserIdAndToUserIdAndStatus(Long fromUserId, Long toUserId, Boolean status) {
        return followersRepository.countByFromUserIdAndToUserIdAndStatus(fromUserId, toUserId, status);
    }
}
