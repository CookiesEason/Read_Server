package com.xzy.read.repository;

import com.xzy.read.entity.MessageLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/07 10:10
 */
public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {

    Page<MessageLike> findAllByToUserId(Long userId, Pageable pageable);

    List<MessageLike> findAllByToUserIdAndIsRead(Long userId, Boolean isRead);


    @Query(value = "select count(*) from message_like where to_user_id = :id and is_read = false", nativeQuery = true)
    Long countUnreadMessage(@Param("id") Long id);

}
