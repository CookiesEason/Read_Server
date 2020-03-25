package com.xzy.read.repository;

import com.xzy.read.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author XieZhongYi
 * 2020/03/23 16:15
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByTelephone(String telephone);

    User findByNickname(String nickname);

    @Query(value = "select id from user where telephone = :telephone",nativeQuery = true)
    Long findIdByTelephone(String telephone);

}
