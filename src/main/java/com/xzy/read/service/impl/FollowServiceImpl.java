package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.FollowerDTO;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.entity.Followers;
import com.xzy.read.entity.Follows;
import com.xzy.read.entity.User;
import com.xzy.read.entity.enums.FollowType;
import com.xzy.read.repository.FollowersRepository;
import com.xzy.read.repository.FollowsRepository;
import com.xzy.read.repository.UserRepository;
import com.xzy.read.service.FollowService;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author XieZhongYi
 * 2020/03/28 20:45
 */
@Service
public class FollowServiceImpl implements FollowService {

    private FollowersRepository followersRepository;

    private FollowsRepository followsRepository;

    private UserRepository userRepository;

    public FollowServiceImpl(FollowersRepository followersRepository, FollowsRepository followsRepository, UserRepository userRepository) {
        this.followersRepository = followersRepository;
        this.followsRepository = followsRepository;
        this.userRepository = userRepository;
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

    @Override
    public ResultVo follow(Follows follows, FollowType followType) {
        Follows old = followsRepository.findByTypeIdAndUserIdAndFollowType(follows.getTypeId(),
                follows.getUserId(),followType);
        if (old != null) {
            old.setStatus(!old.getStatus());
            followsRepository.save(old);
            return ResultVoUtil.success();
        }
        follows.setStatus(true);
        follows.setFollowType(followType);
        followsRepository.save(follows);
        return ResultVoUtil.success();
    }

    @Override
    public Long countFansByType(Long typeId, FollowType followType) {
        return followsRepository.countByTypeIdAndFollowTypeAndStatus(typeId, followType, true);
    }

    @Override
    public ResultVo followerByType(int page, int size, Long typeId,FollowType followType) {
        Page<Follows> followsPage = followsRepository.findAllByTypeIdAndStatusAndFollowType(
                typeId,true,
                followType,
                PageRequest.of(page-1,size, Sort.by(Sort.Direction.DESC,"createdDate"))
        );
        List<FollowerDTO> followerDTOS = new ArrayList<>();
        for (Follows follows : followsPage.toList()) {
            Optional<User> userOptional = userRepository.findById(follows.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                FollowerDTO dto = new FollowerDTO(user.getId(), user.getHeadUrl(), user.getNickname(), follows.getCreatedDate());
                followerDTOS.add(dto);
            } else {
                return ResultVoUtil.error(0, "系统出错");
            }
        }
        PageDTO<FollowerDTO>followerDTOPageDTO = new PageDTO<>(followerDTOS,
                followsPage.getTotalElements(),followsPage.getTotalPages());
        return ResultVoUtil.success(followerDTOPageDTO);
    }
}
