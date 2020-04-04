package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.FollowWorkDTO;
import com.xzy.read.dto.FollowerDTO;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.FollowType;
import com.xzy.read.repository.*;
import com.xzy.read.service.FollowService;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
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

    private TopicArticleRepository topicArticleRepository;

    private ArticleRepository articleRepository;

    private TopicRepository topicRepository;

    private NoteBooksRepository noteBooksRepository;

    public FollowServiceImpl(FollowersRepository followersRepository, FollowsRepository followsRepository, UserRepository userRepository, TopicArticleRepository topicArticleRepository, ArticleRepository articleRepository, TopicRepository topicRepository, NoteBooksRepository noteBooksRepository) {
        this.followersRepository = followersRepository;
        this.followsRepository = followsRepository;
        this.userRepository = userRepository;
        this.topicArticleRepository = topicArticleRepository;
        this.articleRepository = articleRepository;
        this.topicRepository = topicRepository;
        this.noteBooksRepository = noteBooksRepository;
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

    @Override
    public ResultVo findAllTopicsByUserId(Long userId, int page) {
        Page<Follows> followsPage = followsRepository.
                findAllByUserIdAndStatusOrderByCreatedDateDesc(userId, true,
                        PageRequest.of(page-1,4));
        List<FollowWorkDTO> followWorkDTOS = new ArrayList<>();
        for (Follows follows : followsPage.toList()) {
            Long articles, folllowers;
            boolean isFollowed;
            if (follows.getFollowType().equals(FollowType.TOPIC)) {
                 Topic topic = topicRepository.getOne(follows.getTypeId());
                 articles = topicArticleRepository.countByTopicIdAndIsPassed(follows.getTypeId(), true);
                 folllowers = followsRepository.countByTypeIdAndFollowTypeAndStatus(follows.getTypeId(),
                         FollowType.TOPIC,true);
                 isFollowed = followsRepository.existsByTypeIdAndFollowTypeAndStatusAndUserId(
                         follows.getTypeId(),FollowType.TOPIC,true,
                         userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName())
                 );
                FollowWorkDTO followWorkDTO = new FollowWorkDTO(topic.getId(), topic.getName(), topic.getHeadUrl(),
                        articles, folllowers, isFollowed, false);
                followWorkDTOS.add(followWorkDTO);
            } else {
                 NoteBooks noteBooks = noteBooksRepository.getOne(follows.getTypeId());
                 articles = articleRepository.countByNotebookId(follows.getTypeId());
                 folllowers = followsRepository.countByTypeIdAndFollowTypeAndStatus(follows.getTypeId(),
                        FollowType.NOTEBOOK,true);
                 isFollowed = followsRepository.existsByTypeIdAndFollowTypeAndStatusAndUserId(
                        follows.getTypeId(),FollowType.NOTEBOOK,true,
                        userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName())
                 );
                FollowWorkDTO followWorkDTO = new FollowWorkDTO(noteBooks.getId(), noteBooks.getName(), null,
                        articles, folllowers, isFollowed, true);
                followWorkDTOS.add(followWorkDTO);
            }
        }
        PageDTO<FollowWorkDTO> followWorkDTOPageDTO = new PageDTO<>(followWorkDTOS,
                followsPage.getTotalElements(), followsPage.getTotalPages());
        return ResultVoUtil.success(followWorkDTOPageDTO);
    }
}
