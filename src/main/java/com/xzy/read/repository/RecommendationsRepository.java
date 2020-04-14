package com.xzy.read.repository;

import com.xzy.read.entity.Recommendations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/14 11:57
 */
public interface RecommendationsRepository extends JpaRepository<Recommendations, Long> {

    List<Recommendations> findAllByUserId(Long userId);

    Page<Recommendations> getAllByUserId(Long userId, Pageable pageable);

}
