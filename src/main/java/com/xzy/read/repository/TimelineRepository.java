package com.xzy.read.repository;

import com.xzy.read.entity.Timeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author XieZhongYi
 * 2020/04/06 11:09
 */
public interface TimelineRepository extends JpaRepository<Timeline, Long> {

    Page<Timeline> findAllByToUserId(Long userId, Pageable pageable);

}
