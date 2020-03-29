package com.xzy.read.repository;

import com.xzy.read.entity.Followers;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author XieZhongYi
 * 2020/03/28 20:43
 */
public interface FollowersRepository extends JpaRepository<Followers, Long> {

    Long countAllByFromUserIdAndStatus(Long fromUserId, Boolean status);

    Long countAllByToUserIdAndStatus(Long toUserId, Boolean status);

    Followers findByFromUserId(Long fromUserId);

    Long countByFromUserIdAndToUserIdAndStatus(Long fromUserId, Long toUserId, Boolean status);

}
