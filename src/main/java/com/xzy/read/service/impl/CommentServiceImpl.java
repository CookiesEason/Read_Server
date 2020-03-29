package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.CommentDTO;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.dto.SimpleCommentDTO;
import com.xzy.read.dto.SimpleReplyDTO;
import com.xzy.read.entity.Comment;
import com.xzy.read.entity.Reply;
import com.xzy.read.entity.User;
import com.xzy.read.repository.CommentRepository;
import com.xzy.read.service.CommentService;
import com.xzy.read.service.ReplyService;
import com.xzy.read.service.UserService;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author XieZhongYi
 * 2020/03/29 18:53
 */
@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    private UserService userService;

    private ReplyService replyService;

    public CommentServiceImpl(CommentRepository commentRepository, UserService userService, ReplyService replyService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.replyService = replyService;
    }

    @Override
    public ResultVo comment(Comment comment) {
        comment = commentRepository.save(comment);
        User user = userService.findById(comment.getUserId());
        SimpleCommentDTO simpleCommentDTO = new SimpleCommentDTO(
                user.getId(),user.getNickname(), user.getHeadUrl(), comment.getContent(),comment.getCreatedDate()
        );
        return ResultVoUtil.success(simpleCommentDTO);
    }

    @Override
    public ResultVo deleteComment(Comment comment) {
        commentRepository.deleteById(comment.getId());
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo getCommentsByArticleId(Long articleId, int page) {
        Page<Comment> commentPage = commentRepository.findAllByArticleId(articleId,
                PageRequest.of(page-1, 10,Sort.by(Sort.Direction.DESC,"createdDate")));
        List<Comment> commentList = commentPage.toList();
        List<CommentDTO> all = new ArrayList<>();
        List<SimpleReplyDTO> simpleReplyDTOS = new ArrayList<>();
        for (Comment comment : commentList) {
            User user = userService.findById(comment.getUserId());
            SimpleCommentDTO simpleCommentDTO = new SimpleCommentDTO(
                    user.getId(),user.getNickname(), user.getHeadUrl(), comment.getContent(),comment.getCreatedDate()
            );
            List<Reply> replyList = replyService.findAllByCommentId(comment.getId());
            for (Reply reply : replyList) {
                User fromUser = userService.findById(reply.getFromUserId());
                User toUser = userService.findById(reply.getToUserId());
                SimpleReplyDTO simpleReplyDTO = new SimpleReplyDTO(
                        fromUser.getId(),fromUser.getNickname(),fromUser.getHeadUrl(),
                        toUser.getId(),toUser.getNickname(),
                        reply.getContent(),reply.getCreatedDate()
                );
                simpleReplyDTOS.add(simpleReplyDTO);
            }
            CommentDTO commentDTO = new CommentDTO(simpleCommentDTO, simpleReplyDTOS);
            all.add(commentDTO);
        }
        PageDTO<CommentDTO> comments = new PageDTO<>(all,commentPage.getTotalElements(),commentPage.getTotalPages());
        return ResultVoUtil.success(comments);
    }
}
