package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.dto.SearchNotebookDTO;
import com.xzy.read.dto.SearchTopicDTO;
import com.xzy.read.dto.UserInfoDTO;
import com.xzy.read.entity.NoteBooks;
import com.xzy.read.entity.Topic;
import com.xzy.read.entity.User;
import com.xzy.read.entity.enums.FollowType;
import com.xzy.read.repository.*;
import com.xzy.read.service.SearchService;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/08 14:14
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final String USER = "user";
    private static final String NOTEBOOK = "notebook";
    private static final String TOPIC = "topic";

    private UserRepository userRepository;

    private TopicRepository topicRepository;

    private NoteBooksRepository noteBooksRepository;

    private ArticleRepository articleRepository;

    private FollowersRepository followersRepository;

    private TopicArticleRepository topicArticleRepository;

    private FollowsRepository followsRepository;

    public SearchServiceImpl(UserRepository userRepository, TopicRepository topicRepository, NoteBooksRepository noteBooksRepository, ArticleRepository articleRepository, FollowersRepository followersRepository, TopicArticleRepository topicArticleRepository, FollowsRepository followsRepository) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.noteBooksRepository = noteBooksRepository;
        this.articleRepository = articleRepository;
        this.followersRepository = followersRepository;
        this.topicArticleRepository = topicArticleRepository;
        this.followsRepository = followsRepository;
    }


    @Override
    public ResultVo search(String content, int page, String type) {
        if (USER.equals(type)) {
            return searchUsers(content, page);
        } else if (NOTEBOOK.equals(type)) {
            return searchNotebooks(content, page);
        } else if (TOPIC.equals(type)) {
            return searchTopics(content, page);
        }
        return ResultVoUtil.success();
    }

    private ResultVo searchUsers(String content, int page) {
        Page<User> userPage = userRepository.findAllByNicknameLike("%" + content + "%",
                PageRequest.of(page-1,10));
        List<UserInfoDTO> userInfoDTOS = new ArrayList<>();
        Long loginUserId = userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName());
        for (User user : userPage.toList()) {
            Long userId = user.getId();
            Long followers = followersRepository.countAllByFromUserIdAndStatus(userId, true);
            Long fans = followersRepository.countAllByToUserIdAndStatus(userId, true);
            Long articles = articleRepository.countAllByUserIdAndIsDeleted(userId, false);
            Long words = articleRepository.countWordsByUserId(userId);
            Long likes = articleRepository.countLikesByUserId(userId);
            boolean isFollowed = followersRepository.countByFromUserIdAndToUserIdAndStatus(
                    loginUserId, userId, true) > 0;
            UserInfoDTO userInfoDTO = new UserInfoDTO(userId, user.getHeadUrl(), user.getNickname(),
                    user.getIntroduce(),
                    followers,fans,articles,words, likes ,isFollowed);
            userInfoDTOS.add(userInfoDTO);
        }
        PageDTO<UserInfoDTO> dto = new PageDTO<>(userInfoDTOS, userPage.getTotalElements(), userPage.getTotalPages());
        return ResultVoUtil.success(dto);
    }


    private ResultVo searchTopics(String content, int page) {
        Page<Topic> topicPage = topicRepository.findAllByNameLikeOrIntroduceLike("%" + content + "%",
                "%" + content + "%", PageRequest.of(page-1,10));
        Long userId = userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName());
        List<SearchTopicDTO> searchTopicDTOS = new ArrayList<>();
        for (Topic topic : topicPage.toList()) {
            boolean isFollowed = false;
            if (userId != null) {
                isFollowed =  followsRepository.existsByTypeIdAndFollowTypeAndStatusAndUserId(topic.getId(),FollowType.TOPIC,true, userId);
            }
            SearchTopicDTO searchTopicDTO = new SearchTopicDTO(
                    topic.getId(),topic.getHeadUrl(),topic.getName(),
                    topicArticleRepository.countByTopicIdAndIsPassed(topic.getId(),true),
                    followsRepository.countByTypeIdAndFollowTypeAndStatus(topic.getId(), FollowType.TOPIC, true),
                    isFollowed
            );
            searchTopicDTOS.add(searchTopicDTO);
        }
        PageDTO<SearchTopicDTO> dto = new PageDTO<>(searchTopicDTOS, topicPage.getTotalElements(), topicPage.getTotalPages());
        return ResultVoUtil.success(dto);
    }

    private ResultVo searchNotebooks(String content, int page) {
        Page<NoteBooks> noteBooksPage = noteBooksRepository.findAllByNameLike("%" + content + "%",
                PageRequest.of(page-1, 10));
        List<SearchNotebookDTO> searchNotebookDTOS = new ArrayList<>();
        Long userId = userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName());
        for (NoteBooks noteBooks : noteBooksPage.toList()) {
            Long articles = articleRepository.countByNotebookId(noteBooks.getId());
            Long followers = followsRepository.countByTypeIdAndFollowTypeAndStatus(noteBooks.getId(), FollowType.NOTEBOOK, true);
            SearchNotebookDTO searchNotebookDTO = new SearchNotebookDTO(
                    noteBooks.getId(), noteBooks.getName(), articles, followers,
                    followsRepository.existsByTypeIdAndFollowTypeAndStatusAndUserId(noteBooks.getId(),FollowType.NOTEBOOK,
                            true, userId)
            );
            searchNotebookDTOS.add(searchNotebookDTO);
        }
        PageDTO<SearchNotebookDTO> dto = new PageDTO<>(searchNotebookDTOS, noteBooksPage.getTotalElements(), noteBooksPage.getTotalPages());
        return ResultVoUtil.success(dto);
    }

}
