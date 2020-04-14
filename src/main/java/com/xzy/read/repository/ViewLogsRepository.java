package com.xzy.read.repository;

import com.xzy.read.entity.ViewLogs;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author XieZhongYi
 * 2020/04/13 21:18
 */
public interface ViewLogsRepository extends JpaRepository<ViewLogs, Long> {
    ViewLogs findByUserIdAndArticleId(Long userId, Long articleId);
}
