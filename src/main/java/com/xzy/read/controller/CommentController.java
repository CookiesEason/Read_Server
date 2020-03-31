package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Comment;
import com.xzy.read.entity.Likes;
import com.xzy.read.entity.Reply;
import com.xzy.read.service.CommentService;
import com.xzy.read.service.ReplyService;
import org.springframework.web.bind.annotation.*;

/**
 * @author XieZhongYi
 * 2020/03/29 19:02
 */
@RestController
@RequestMapping("/api")
public class CommentController {

    private CommentService commentService;

    private ReplyService replyService;

    public CommentController(CommentService commentService, ReplyService replyService) {
        this.commentService = commentService;
        this.replyService = replyService;
    }

    @GetMapping("/comment")
    public ResultVo getComment(Long articleId,int page) {
        return commentService.getCommentsByArticleId(articleId, page);
    }

    @PostMapping("/comment")
    public ResultVo comment(@RequestBody Comment comment) {
        return commentService.comment(comment);
    }

    @DeleteMapping("/comment")
    public ResultVo deleteComment(@RequestBody Comment comment) {
        return commentService.deleteComment(comment);
    }

    @PutMapping("/comment/like")
    public void likeComment(@RequestBody Likes likes) {
        commentService.like(likes);
    }

    @PostMapping("/reply")
    public ResultVo reply(@RequestBody Reply reply) {
        return replyService.reply(reply);
    }

    @DeleteMapping("/reply")
    public ResultVo deleteReply(@RequestBody Reply reply) {
        return replyService.deleteReply(reply);
    }

    @PutMapping("/reply/like")
    public void likeReply(@RequestBody Likes likes) {
        replyService.like(likes);
    }

}
