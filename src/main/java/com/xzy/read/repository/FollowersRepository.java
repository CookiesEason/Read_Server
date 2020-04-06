package com.xzy.read.repository;

import com.xzy.read.entity.Followers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/28 20:43
 */
public interface FollowersRepository extends JpaRepository<Followers, Long> {

    Long countAllByFromUserIdAndStatus(Long fromUserId, Boolean status);

    Long countAllByToUserIdAndStatus(Long toUserId, Boolean status);

    Followers findByFromUserId(Long fromUserId);

    Long countByFromUserIdAndToUserIdAndStatus(Long fromUserId, Long toUserId, Boolean status);

    Page<Followers> findAllByFromUserIdAndStatus(Long fromUserId, Boolean status, Pageable pageable);

    Page<Followers> findAllByToUserIdAndStatus(Long toUserId, Boolean status, Pageable pageable);

    List<Followers> findAllByFromUserIdAndStatus(Long toUserId, Boolean status);

}
