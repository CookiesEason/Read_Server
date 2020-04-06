package com.xzy.read.repository;

import com.xzy.read.entity.Follows;
import com.xzy.read.entity.enums.FollowType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/01 19:14
 */
public interface FollowsRepository extends JpaRepository<Follows, Long> {

    Follows findByTypeIdAndUserIdAndFollowType(Long typeId, Long userId, FollowType followType);

    Long countByTypeIdAndFollowTypeAndStatus(Long typeId, FollowType followType, Boolean status);

    Boolean existsByTypeIdAndFollowTypeAndStatusAndUserId(Long typeId, FollowType followType, Boolean status, Long userId);

    Page<Follows> findAllByTypeIdAndStatusAndFollowType(Long typeId, Boolean status, FollowType followType, Pageable pageable);

    Page<Follows> findAllByUserIdAndStatusOrderByCreatedDateDesc(Long userId, Boolean status, Pageable pageable);

    List<Follows> findAllByUserIdAndStatus(Long userId, Boolean status);

    List<Follows> findAllByUserIdAndStatusAndFollowType(Long userId, Boolean status,FollowType followType);

}
