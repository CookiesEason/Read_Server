package com.xzy.read.repository;

import com.xzy.read.entity.MessageOther;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/07 21:20
 */
public interface MessageOtherRepository extends JpaRepository<MessageOther, Long> {

    Page<MessageOther> findAllByToUserId(Long userId, Pageable pageable);

    List<MessageOther> findAllByToUserIdAndIsRead(Long userId, Boolean isRead);

    @Query(value = "select count(*) from message_other where to_user_id = :id and is_read = false", nativeQuery = true)
    Long countUnreadMessage(@Param("id") Long id);

}
