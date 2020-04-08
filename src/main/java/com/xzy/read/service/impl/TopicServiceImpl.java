package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.*;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.FollowType;
import com.xzy.read.repository.*;
import com.xzy.read.service.TopicService;
import com.xzy.read.service.UserService;
import com.xzy.read.util.ResultVoUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author XieZhongYi
 * 2020/04/01 14:15
 */
@Service
public class TopicServiceImpl implements TopicService {

    private TopicRepository topicRepository;

    private TopicArticleRepository topicArticleRepository;

    private UserService userService;

    private ArticleRepository articleRepository;

    private FollowsRepository followsRepository;

    private MessageOtherRepository messageOtherRepository;

    public TopicServiceImpl(TopicRepository topicRepository, TopicArticleRepository topicArticleRepository, UserService userService, ArticleRepository articleRepository, FollowsRepository followsRepository, MessageOtherRepository messageOtherRepository) {
        this.topicRepository = topicRepository;
        this.topicArticleRepository = topicArticleRepository;
        this.userService = userService;
        this.articleRepository = articleRepository;
        this.followsRepository = followsRepository;
        this.messageOtherRepository = messageOtherRepository;
    }

    @Override
    public ResultVo getInfoById(Long id) {
        Optional<Topic> topicOptional = topicRepository.findById(id);
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            boolean isFollowed = false;
            Long userId = userService.getUserId();
            if (userId != null) {
                isFollowed =  followsRepository.existsByTypeIdAndFollowTypeAndStatusAndUserId(topic.getId(),FollowType.TOPIC,true, userId);
            }
            User u = userService.findById(topic.getUserId());
            TopicDTO topicDTO = new TopicDTO(topic.getId(), topic.getHeadUrl(), topic.getName(),
                    topic.getIntroduce(),topic.getUserId(),u.getHeadUrl(),u.getNickname(),
                    topicArticleRepository.countByTopicIdAndIsPassed(topic.getId(),true),
                    followsRepository.countByTypeIdAndFollowTypeAndStatus(topic.getId(), FollowType.TOPIC, true),
                    isFollowed, topic.getIsSubmit(), topic.getIsVerify());
            return ResultVoUtil.success(topicDTO);
        }
        return ResultVoUtil.error(0,"专题不存在");
    }

    @Override
    public List<Topic> getAllTopicsByUserId(Long userId) {
        return topicRepository.findAllByUserId(userId);
    }

    @Override
    public ResultVo save(Topic topic) {
        topic =  topicRepository.save(topic);
        return ResultVoUtil.success(topic.getId());
    }

    @Override
    public ResultVo delete(Topic topic) {
        topicRepository.deleteById(topic.getId());
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo search(Long articleId,String name) {
        List<Topic> topics = topicRepository.findAllByNameLikeAndUserId("%"+name+"%",userService.getUserId());
        List<SimpleArticleTopicDTO> list = new ArrayList<>();
        for (Topic topic : topics) {
            SimpleArticleTopicDTO simpleArticleTopicDTO = new SimpleArticleTopicDTO(topic.getId(),
                    topic.getHeadUrl(), topic.getName(),
                    topicArticleRepository.existsByArticleIdAndIsPassedAndTopicId(articleId, true,topic.getId()));
            list.add(simpleArticleTopicDTO);
        }
        return ResultVoUtil.success(list);
    }

    @Override
    public ResultVo findTopicsByArticleId(Long articleId, int page) {
        Page<TopicArticle> topicArticlePage = topicArticleRepository
                .findAllByArticleIdAndIsPassed(articleId,true, PageRequest.of(page-1,1));
        List<Topic> topics = new ArrayList<>();
        for (TopicArticle topicArticle : topicArticlePage.toList()) {
           Optional<Topic> topicOptional =  topicRepository.findById(topicArticle.getTopicId());
            topicOptional.ifPresent(topics::add);
        }
        PageDTO<Topic> topicPageDTO = new PageDTO<>(topics, topicArticlePage.getTotalElements(), topicArticlePage.getTotalPages());
        return ResultVoUtil.success(topicPageDTO);
    }

    @Override
    public ResultVo collect(TopicArticle topicArticle) {
        topicArticle.setIsPassed(true);
        topicArticle.setUserId(userService.getUserId());
        topicArticleRepository.save(topicArticle);
        return ResultVoUtil.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo remove(TopicArticle topicArticle) {
        topicArticleRepository.deleteByArticleIdAndTopicId(topicArticle.getArticleId(), topicArticle.getTopicId());
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo submit(TopicArticle topicArticle) {
        boolean isExisted = topicArticleRepository.
                existsByArticleIdAndTopicId(topicArticle.getArticleId(), topicArticle.getTopicId());
        if(isExisted) {
            return ResultVoUtil.error(0,"请勿重复投稿");
        }
        Optional<Topic> topicOptional = topicRepository.findById(topicArticle.getTopicId());
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            if (topic.getIsSubmit()) {
                if (!topic.getIsVerify()) {
                    topicArticle.setIsPassed(true);
                } else {
                    topicArticle.setIsPassed(false);
                }
                topicArticle.setUserId(topic.getUserId());
                topicArticleRepository.save(topicArticle);
                return ResultVoUtil.success();
            } else {
                return ResultVoUtil.error(0, "该专题不接受投稿");
            }
        }
        return ResultVoUtil.error(0, "该专题不存在");
    }

    @Override
    public ResultVo needSubmitTopic(Long userId) {
        List<Topic> topics = topicRepository.findAllByUserIdAndIsVerifyAndIsSubmit(userId, true, true);
        List<MessageTopicDTO> messageTopicDTOS = new ArrayList<>();
        for (Topic topic : topics) {
            MessageTopicDTO messageTopicDTO = new MessageTopicDTO(topic.getId(),
                    topic.getName(),topic.getHeadUrl(),
                    topicArticleRepository.countUnpassedArticleForTopic(topic.getId()));
            messageTopicDTOS.add(messageTopicDTO);
        }
        return ResultVoUtil.success(messageTopicDTOS);
    }

    @Override
    public ResultVo allSubmitList(Long userId) {
        List<TopicArticle> topicArticles = topicArticleRepository.findAllByUserIdAndIsPassed(userId, false);
        List<MessageArticleDTO> simpleArticles = new ArrayList<>();
        for (TopicArticle topicArticle : topicArticles) {
            Optional<Article> optionalArticle = articleRepository.findById(topicArticle.getArticleId());
            if (optionalArticle.isPresent()) {
                Article article = optionalArticle.get();
                User user = userService.findById(article.getUserId());
                MessageArticleDTO simpleArticle = new MessageArticleDTO(user.getId(), user.getHeadUrl(), user.getNickname(),
                        article.getCreatedDate(),article.getId(),article.getTitle(),
                        removeHtml(article.getContent()),article.getClicks(),
                        articleRepository.countCommentsByArticleId(article.getId()),article.getLikes(),
                        topicArticle.getIsPassed(),
                        topicArticle.getTopicId());
                simpleArticles.add(simpleArticle);
            }
        }
        return ResultVoUtil.success(simpleArticles);
    }

    @Override
    public ResultVo submitList(Long topicId, Boolean up) {
        List<TopicArticle> topicArticles;
        if (up) {
            topicArticles = topicArticleRepository.findAllByTopicIdAndIsPassed(topicId,false);
        } else {
            topicArticles = topicArticleRepository.findAllByTopicIdOrderByIsPassedAsc(topicId);
        }
        List<MessageArticleDTO> simpleArticles = new ArrayList<>();
        for (TopicArticle topicArticle : topicArticles) {
            Optional<Article> optionalArticle = articleRepository.findById(topicArticle.getArticleId());
            if (optionalArticle.isPresent()) {
                Article article = optionalArticle.get();
                User user = userService.findById(article.getUserId());
                MessageArticleDTO simpleArticle = new MessageArticleDTO(user.getId(), user.getHeadUrl(), user.getNickname(),
                        article.getCreatedDate(),article.getId(),article.getTitle(),
                        removeHtml(article.getContent()),article.getClicks(),
                        articleRepository.countCommentsByArticleId(article.getId()),article.getLikes(),
                        topicArticle.getIsPassed(), topicArticle.getTopicId());
                simpleArticles.add(simpleArticle);
            }
        }
        return ResultVoUtil.success(simpleArticles);
    }

    @Override
    public void verify(RequestTopicArticle requestTopicArticle) {
        TopicArticle t = topicArticleRepository.findByArticleIdAndTopicId(requestTopicArticle.getArticleId(),
                requestTopicArticle.getTopicId());
        MessageOther messageOther = new MessageOther();
        messageOther.setArticleId(t.getArticleId());
        messageOther.setTopicId(t.getTopicId());
        messageOther.setToUserId(articleRepository.getOne(t.getArticleId()).getUserId());
        messageOther.setIsRead(false);
        messageOther.setReason(requestTopicArticle.getReason());
        if (requestTopicArticle.getIsPassed()) {
            messageOther.setIsRejected(false);
            t.setIsPassed(requestTopicArticle.getIsPassed());
            topicArticleRepository.save(t);
        } else {
            messageOther.setIsRejected(true);
            topicArticleRepository.delete(t);
        }
        messageOtherRepository.save(messageOther);
    }

    @Override
    public ResultVo getAllArticles(Long topicId,int page) {
        Page<TopicArticle> topicArticlePage = topicArticleRepository.findAllByTopicIdAndIsPassed(topicId,
                true, PageRequest.of(page-1,4, Sort.by(Sort.Direction.DESC,"id")));
        List<SimpleArticleDTO> articleDTOS = new ArrayList<>();
        for (TopicArticle topicArticle : topicArticlePage.toList()) {
            Optional<Article> articleOptional = articleRepository.findById(topicArticle.getArticleId());
            if (articleOptional.isPresent()) {
                Article article = articleOptional.get();
                User articleUser = userService.findById(article.getUserId());
                SimpleArticleDTO simpleArticleDTO = new SimpleArticleDTO(
                        article.getId(),article.getTitle(),removeHtml(article.getContent()),
                        articleUser.getId(),articleUser.getNickname(),
                        articleRepository.countCommentsByArticleId(article.getId()),article.getLikes()
                );
                articleDTOS.add(simpleArticleDTO);
            }
        }
        PageDTO<SimpleArticleDTO> articles = new PageDTO<>(articleDTOS, topicArticlePage.getTotalElements(),
                topicArticlePage.getTotalPages());
        return ResultVoUtil.success(articles);
    }

    private String removeHtml (String content) {
        if (content == null) {
            return "";
        }
        Document doc = Jsoup.parse(content);
        return doc.text();
    }
}
