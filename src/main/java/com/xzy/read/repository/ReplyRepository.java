package com.xzy.read.repository;

import com.xzy.read.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/29 19:41
 */
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findAllByCommentId(Long commentId);

}
