package com.xzy.read.repository;

import com.xzy.read.entity.SearchArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author XieZhongYi
 * 2020/04/09 20:28
 */
public interface SearchArticleRepository extends ElasticsearchRepository<SearchArticle, Long> {

    Page<SearchArticle> findAllByTitleOrContent(String title, String content, Pageable pageable);

}
