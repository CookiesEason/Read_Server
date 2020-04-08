package com.xzy.read.repository;

import com.xzy.read.entity.TopicArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    Page<TopicArticle> findAllByArticleIdAndIsPassed(Long articleId, Boolean isPassed, Pageable pageable);

    List<TopicArticle> findAllByUserIdAndIsPassed(Long userId, Boolean isPassed);

    List<TopicArticle> findAllByTopicIdOrderByIsPassedAsc(Long topicId);

    @Query(value = "select count(*) from topic_article where topic_id = :id and is_passed = false", nativeQuery = true)
    Long countUnpassedArticleForTopic(@Param("id") Long id);

    @Query(value = "select count(*) from topic_article where user_id = :id and is_passed = false", nativeQuery = true)
    Long countUnpassedArticle(@Param("id") Long id);

    TopicArticle findByArticleIdAndTopicId(Long articleId,Long topicId);

}
