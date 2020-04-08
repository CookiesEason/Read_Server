package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.MessageComment;
import com.xzy.read.entity.MessageFollow;
import com.xzy.read.entity.MessageLike;

/**
 * @author XieZhongYi
 * 2020/04/07 10:15
 */
public interface MessageService {

    ResultVo countUnreadMessage(Long userId);

    ResultVo getCommentMessages(Long userId, int page);

    ResultVo getLikeMessages(Long userId, int page);

    ResultVo getFollowMessages(Long userId, int page);

    ResultVo getOthersMessages(Long userId, int page);

    /**
     * 评论
     * @param messageComment
     */
    void sendMessage(MessageComment messageComment);

    /**
     * 关注
     * @param messageFollow
     */
    void sendMessage(MessageFollow messageFollow);

    /**
     * 点赞和喜欢
     * @param messageLike
     */
    void sendMessage(MessageLike messageLike);

}
