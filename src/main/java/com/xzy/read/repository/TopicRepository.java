package com.xzy.read.repository;

import com.xzy.read.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/01 14:12
 */
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findAllByNameLikeAndUserId(String name, Long userId);

    List<Topic> findAllByUserId(Long userId);

    List<Topic> findAllByUserIdAndIsVerifyAndIsSubmit(Long userId, Boolean isVerify, Boolean isSubmit);
}
