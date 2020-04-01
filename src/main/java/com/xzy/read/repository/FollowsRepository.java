package com.xzy.read.repository;

import com.xzy.read.entity.Follows;
import com.xzy.read.entity.enums.FollowType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author XieZhongYi
 * 2020/04/01 19:14
 */
public interface FollowsRepository extends JpaRepository<Follows, Long> {

    Follows findByTypeIdAndUserIdAndFollowType(Long typeId, Long userId, FollowType followType);

    Long countByTypeIdAndFollowType(Long typeId, FollowType followType);

    Page<Follows> findAllByTypeIdAndStatusAndFollowType(Long typeId, Boolean status, FollowType followType, Pageable pageable);

}
