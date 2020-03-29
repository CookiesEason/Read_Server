package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Reply;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/29 19:43
 */
public interface ReplyService {

    ResultVo reply(Reply reply);

    ResultVo deleteReply(Reply reply);

    List<Reply> findAllByCommentId(Long commentId);

}
