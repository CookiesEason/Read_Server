package com.xzy.read.repository;

import com.xzy.read.entity.TopicArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/01 14:23
 */
public interface TopicArticleRepository extends JpaRepository<TopicArticle, Long> {

    Boolean existsByArticleIdAndIsPassedAndTopicId(Long articleId, Boolean isPassed, Long topicId);

    Boolean existsByArticleIdAndTopicId(Long articleId, Long topicId);

    void deleteByArticleIdAndTopicId(Long articleId, Long topicId);

    Long countByTopicIdAndIsPassed(Long topicId, Boolean isPassed);

    List<TopicArticle> findAllByTopicIdAndIsPassed(Long topicId, Boolean isPassed);

    Page<TopicArticle> findAllByTopicIdAndIsPassed(Long topicId, Boolean isPassed, Pageable pageable);

}
