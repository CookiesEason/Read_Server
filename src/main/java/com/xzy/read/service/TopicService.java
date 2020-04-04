package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Topic;
import com.xzy.read.entity.TopicArticle;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/01 14:13
 */
public interface TopicService {

    ResultVo getInfoById(Long id);

    List<Topic> getAllTopicsByUserId(Long userId);

    ResultVo getAllArticles(Long topicId,int page);

    ResultVo save(Topic topic);

    ResultVo delete(Topic topic);

    ResultVo search(Long articleId,String name);


    ResultVo findTopicsByArticleId(Long articleId, int page);

    /**
     * 收入文章
     * @param topicArticle
     * @return
     */
    ResultVo collect(TopicArticle topicArticle);

    /**
     * 移除文章
     * @param topicArticle
     * @return
     */
    ResultVo remove(TopicArticle topicArticle);

    /**
     * 投稿
     */
    ResultVo submit(TopicArticle topicArticle);

    /**
     * 投稿列表
     */
    ResultVo submitList(Long topicId);
}
