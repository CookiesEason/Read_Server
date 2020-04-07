package com.xzy.read.service.impl;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.*;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.FollowType;
import com.xzy.read.entity.enums.MessageType;
import com.xzy.read.repository.*;
import com.xzy.read.service.FollowService;
import com.xzy.read.service.MessageService;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    private TimelineRepository timelineRepository;

    private MessageService messageService;

    public FollowServiceImpl(FollowersRepository followersRepository, FollowsRepository followsRepository, UserRepository userRepository, TopicArticleRepository topicArticleRepository, ArticleRepository articleRepository, TopicRepository topicRepository, NoteBooksRepository noteBooksRepository, TimelineRepository timelineRepository, MessageService messageService) {
        this.followersRepository = followersRepository;
        this.followsRepository = followsRepository;
        this.userRepository = userRepository;
        this.topicArticleRepository = topicArticleRepository;
        this.articleRepository = articleRepository;
        this.topicRepository = topicRepository;
        this.noteBooksRepository = noteBooksRepository;
        this.timelineRepository = timelineRepository;
        this.messageService = messageService;
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
        MessageFollow messageFollow = new MessageFollow();
        messageFollow.setTypeId(followers.getToUserId());
        messageFollow.setFromUserId(followers.getFromUserId());
        messageFollow.setToUserId(followers.getToUserId());
        messageFollow.setMessageType(MessageType.USER);
        messageService.sendMessage(messageFollow);
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
    public ResultVo findAllFansByUserId(Long userId, int page) {
        Page<Followers> followersPageDTO = followersRepository.findAllByToUserIdAndStatus(
                userId, true, PageRequest.of(page - 1, 5,Sort.by(Sort.Direction.DESC, "id"))
        );
        List<FollowUserDTO> followUserDTOS = new ArrayList<>();
        for (Followers followers : followersPageDTO.toList()) {
            User user = userRepository.getOne(followers.getFromUserId());
            FollowUserDTO followUserDTO = new FollowUserDTO(user.getId(),user.getHeadUrl(),user.getNickname(),
                    countfollowers(user.getId()), countFans(user.getId()), articleRepository.countAllByUserIdAndIsDeleted(user.getId(), false),
                    articleRepository.countWordsByUserId(user.getId()),
                    articleRepository.countLikesByUserId(user.getId()),
                    countByFromUserIdAndToUserIdAndStatus(userId, user.getId(), true) > 0);
            followUserDTOS.add(followUserDTO);
        }
        PageDTO<FollowUserDTO> followUserDTOPageDTO = new PageDTO<>(followUserDTOS, followersPageDTO.getTotalElements(),
                followersPageDTO.getTotalPages());
        return ResultVoUtil.success(followUserDTOPageDTO);
    }

    @Override
    public ResultVo findAllFollowersByUserId(Long userId, int page) {
        Page<Followers> followersPageDTO = followersRepository.findAllByFromUserIdAndStatus(
                userId, true, PageRequest.of(page - 1, 5,Sort.by(Sort.Direction.DESC, "id"))
        );
        List<FollowUserDTO> followUserDTOS = new ArrayList<>();
        for (Followers followers : followersPageDTO.toList()) {
            User user = userRepository.getOne(followers.getToUserId());
            FollowUserDTO followUserDTO = new FollowUserDTO(user.getId(),user.getHeadUrl(),user.getNickname(),
                    countfollowers(user.getId()), countFans(user.getId()), articleRepository.countAllByUserIdAndIsDeleted(user.getId(), false),
                    articleRepository.countWordsByUserId(user.getId()),
                    articleRepository.countLikesByUserId(user.getId()),
                    countByFromUserIdAndToUserIdAndStatus(userId, user.getId(), true) > 0);
            followUserDTOS.add(followUserDTO);
        }
        PageDTO<FollowUserDTO> followUserDTOPageDTO = new PageDTO<>(followUserDTOS, followersPageDTO.getTotalElements(),
                followersPageDTO.getTotalPages());
        return ResultVoUtil.success(followUserDTOPageDTO);
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
        MessageFollow messageFollow = new MessageFollow();
        messageFollow.setTypeId(follows.getTypeId());
        messageFollow.setFromUserId(follows.getUserId());
        if (followType.equals(FollowType.TOPIC)) {
            Topic topic = topicRepository.getOne(follows.getTypeId());
            messageFollow.setMessageType(MessageType.TOPIC);
            messageFollow.setToUserId(topic.getUserId());
        } else {
            NoteBooks noteBooks = noteBooksRepository.getOne(follows.getTypeId());
            messageFollow.setMessageType(MessageType.NOTEBOOK);
            messageFollow.setToUserId(noteBooks.getUserId());
        }
        messageService.sendMessage(messageFollow);
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

    @Override
    public ResultVo findArticlesByTimeline(Long userId, int page) {
        Page<Timeline> timelinePage = timelineRepository.findAllByToUserId(userId,
                PageRequest.of(page-1,4,Sort.by(Sort.Direction.DESC, "id")));
        List<LikeArticleDTO> likeArticleDTOS = new ArrayList<>();
        for (Timeline timeline : timelinePage.toList()) {
            Article article = articleRepository.getOne(timeline.getArticleId());
            User user = userRepository.getOne(article.getUserId());
            LikeArticleDTO articleDTO = new LikeArticleDTO(
                    user.getId(),user.getHeadUrl(),user.getNickname(),
                    article.getCreatedDate(), article.getId(),article.getTitle(), removeHtml(article.getContent()),
                    article.getClicks(),articleRepository.countCommentsByArticleId(article.getId()), article.getLikes()
            );
            likeArticleDTOS.add(articleDTO);
        }
        PageDTO<LikeArticleDTO> likeArticleDTOPageDTO = new PageDTO<>(likeArticleDTOS, timelinePage.getTotalElements(),
                timelinePage.getTotalPages());
        return ResultVoUtil.success(likeArticleDTOPageDTO);
    }

    @Override
    public ResultVo findAllFollowsByUserId(Long userId, String type) {
        List<Follows> followsList;
        if ("notebook".equals(type)) {
            followsList = followsRepository.findAllByUserIdAndStatusAndFollowType(userId, true, FollowType.NOTEBOOK);
        } else if ("topic".equals(type)) {
            followsList = followsRepository.findAllByUserIdAndStatusAndFollowType(userId, true, FollowType.TOPIC);

        } else {
            followsList = followsRepository.findAllByUserIdAndStatus(userId, true);
        }
        List<FollowDTO> follows = new ArrayList<>();
        for (Follows f : followsList) {
            if (f.getFollowType().equals(FollowType.TOPIC)) {
                Topic topic = topicRepository.getOne(f.getTypeId());
                FollowDTO followDTO = new FollowDTO(f.getTypeId(), topic.getName(), topic.getHeadUrl(), false);
                follows.add(followDTO);
            } else {
                NoteBooks books = noteBooksRepository.getOne(f.getTypeId());
                FollowDTO followDTO = new FollowDTO(f.getTypeId(), books.getName(), null, true);
                follows.add(followDTO);
            }
        }
        return ResultVoUtil.success(follows);
    }

    @Override
    public ResultVo findAllFollowersByUserId(Long userId) {
        List<Followers> followers = followersRepository.findAllByFromUserIdAndStatus(userId,true);
        List<SimpleUserDTO> users = new ArrayList<>();
        for (Followers follower : followers) {
            User user = userRepository.getOne(follower.getToUserId());
            SimpleUserDTO simpleUserDTO = new SimpleUserDTO(user.getId(), user.getHeadUrl(), user.getNickname());
            users.add(simpleUserDTO);
        }
        return ResultVoUtil.success(users);
    }

    private String removeHtml (String content) {
        if (content == null) {
            return "";
        }
        Document doc = Jsoup.parse(content);
        return doc.text();
    }
}
