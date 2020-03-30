package com.xzy.read.repository;

import com.xzy.read.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/30 14:12
 */
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    List<Collection> findAllByUserId(Long userId);

    Boolean existsByArticleIdAndUserId(Long articleId, Long userId);

    void deleteByArticleIdAndUserId(Long articleId, Long userId);

}
