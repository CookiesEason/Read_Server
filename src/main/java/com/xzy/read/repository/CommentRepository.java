package com.xzy.read.repository;

import com.xzy.read.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author XieZhongYi
 * 2020/03/29 18:52
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByArticleId(Long articleId, Pageable pageable);

}
