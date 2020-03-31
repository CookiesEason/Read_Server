package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Comment;
import com.xzy.read.entity.Likes;


/**
 * @author XieZhongYi
 * 2020/03/29 18:52
 */
public interface CommentService  {

    ResultVo comment(Comment comment);

    ResultVo deleteComment(Comment comment);

    ResultVo getCommentsByArticleId(Long articleId, int page);

    void like(Likes likes);

}