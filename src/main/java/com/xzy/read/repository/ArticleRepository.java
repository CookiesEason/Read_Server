package com.xzy.read.repository;

import com.xzy.read.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/25 15:57
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findAllByNotebookIdAndIsDeleted(Long notebookId, Boolean isDeleted);

    Article findByIsTop(Boolean isTop);

    List<Article> findAllByIsDeletedAndUserId(Boolean isDelete, Long userId);

    List<Article> findAllByUserIdAndIsPublished(Long userId, Boolean isPublished,Pageable pageable);

}
