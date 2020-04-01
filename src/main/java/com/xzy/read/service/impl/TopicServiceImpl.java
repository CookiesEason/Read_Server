package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.*;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.Topic;
import com.xzy.read.entity.TopicArticle;
import com.xzy.read.entity.User;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.TopicArticleRepository;
import com.xzy.read.repository.TopicRepository;
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

    public TopicServiceImpl(TopicRepository topicRepository, TopicArticleRepository topicArticleRepository, UserService userService, ArticleRepository articleRepository) {
        this.topicRepository = topicRepository;
        this.topicArticleRepository = topicArticleRepository;
        this.userService = userService;
        this.articleRepository = articleRepository;
    }

    @Override
    public ResultVo getInfoById(Long id) {
        Optional<Topic> topicOptional = topicRepository.findById(id);
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            User u = userService.findById(topic.getUserId());
            TopicDTO topicDTO = new TopicDTO(topic.getId(), topic.getHeadUrl(), topic.getName(),
                    topic.getIntroduce(),topic.getUserId(),u.getHeadUrl(),u.getNickname(),
                    topicArticleRepository.countByTopicIdAndIsPassed(topic.getId(),true),
                    0L,false);
            return ResultVoUtil.success(topicDTO);
        }
        return ResultVoUtil.error(0,"专题不存在");
    }

    @Override
    public ResultVo save(Topic topic) {
        topicRepository.save(topic);
        return ResultVoUtil.success();
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
    public ResultVo collect(TopicArticle topicArticle) {
        topicArticle.setIsPassed(true);
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
        Optional<Topic> topicOptional = topicRepository.findById(topicArticle.getTopicId());
        if (topicOptional.isPresent()) {
            Topic topic = topicOptional.get();
            if (topic.getIsSubmit()) {
                if (topic.getIsVerify()) {
                    topicArticle.setIsPassed(true);
                } else {
                    topicArticle.setIsPassed(false);
                    //todo 发送投稿请求
                }
                topicArticleRepository.save(topicArticle);
                return ResultVoUtil.success();
            } else {
                return ResultVoUtil.error(0, "该专题不接受投稿");
            }
        }
        return ResultVoUtil.error(0, "该专题不存在");
    }

    @Override
    public ResultVo submitList(Long topicId) {
        List<TopicArticle> topicArticles = topicArticleRepository.findAllByTopicIdAndIsPassed(topicId,false);
        List<RecommendSimpleArticle> simpleArticles = new ArrayList<>();
        for (TopicArticle topicArticle : topicArticles) {
            Optional<Article> optionalArticle = articleRepository.findById(topicArticle.getArticleId());
            if (optionalArticle.isPresent()) {
                RecommendSimpleArticle simpleArticle = new RecommendSimpleArticle(topicArticle.getArticleId(),
                        optionalArticle.get().getTitle());
                simpleArticles.add(simpleArticle);
            }
        }
        return ResultVoUtil.success(simpleArticles);
    }

    @Override
    public ResultVo getAllArticles(Long topicId,int page) {
        Page<TopicArticle> topicArticlePage = topicArticleRepository.findAllByTopicIdAndIsPassed(topicId,
                true, PageRequest.of(page-1,10, Sort.by(Sort.Direction.DESC,"id")));
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
