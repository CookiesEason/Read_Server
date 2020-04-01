package com.xzy.read.repository;

import com.xzy.read.dto.RecommendUserDTO;
import com.xzy.read.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/23 16:15
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByTelephone(String telephone);

    User findByNickname(String nickname);

    @Query(value = "select id from user where telephone = :telephone",nativeQuery = true)
    Long findIdByTelephone(String telephone);

    @Query(value = "select user.id, user.head_url, user.nickname, user.introduce, user.sex , sum(words) as words, sum(likes) as likes from user,article where user.id = article.user_id group by user.id",
            countQuery = "select count(*) from (select count(*) from user, article where article.user_id = user.id group by user.id) as `all`",nativeQuery = true)
    Page<Object[]> findHotUsers(Pageable pageable);
}
