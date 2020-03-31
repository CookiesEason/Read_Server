package com.xzy.read.repository;

import com.xzy.read.entity.Likes;
import com.xzy.read.entity.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author XieZhongYi
 * 2020/03/29 21:13
 */
public interface LikeRepository extends JpaRepository<Likes, Long> {

    Likes findByTypeIdAndUserIdAndType(Long typeId, Long userId,Type type);

    Boolean existsByTypeIdAndUserIdAndStatusAndType(Long typeId, Long userId, Boolean status,Type type);

    Page<Likes> findAllByTypeIdAndStatusAndType(Long typeId, Boolean status, Pageable pageable, Type type);

}
