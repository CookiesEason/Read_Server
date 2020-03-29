package com.xzy.read.repository;

import com.xzy.read.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author XieZhongYi
 * 2020/03/29 21:13
 */
public interface LikeRepository extends JpaRepository<Likes, Long> {

    Likes findByArticleIdAndUserId(Long articleId, Long userId);

}
