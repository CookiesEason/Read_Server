package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.CommentDTO;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.dto.SimpleCommentDTO;
import com.xzy.read.dto.SimpleReplyDTO;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.MessageType;
import com.xzy.read.entity.enums.Type;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.CommentRepository;
import com.xzy.read.repository.LikeRepository;
import com.xzy.read.service.CommentService;
import com.xzy.read.service.MessageService;
import com.xzy.read.service.ReplyService;
import com.xzy.read.service.UserService;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author XieZhongYi
 * 2020/03/29 18:53
 */
@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    private ArticleRepository articleRepository;

    private UserService userService;

    private ReplyService replyService;

    private LikeRepository likeRepository;

    private MessageService messageService;

    public CommentServiceImpl(CommentRepository commentRepository, ArticleRepository articleRepository, UserService userService, ReplyService replyService, LikeRepository likeRepository, MessageService messageService) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.userService = userService;
        this.replyService = replyService;
        this.likeRepository = likeRepository;
        this.messageService = messageService;
    }

    @Override
    public ResultVo comment(Comment comment) {
        comment = commentRepository.save(comment);
        User user = userService.findById(comment.getUserId());
        SimpleCommentDTO simpleCommentDTO = new SimpleCommentDTO(
                user.getId(),user.getNickname(), user.getHeadUrl(), comment.getId(),comment.getContent(),
                false,comment.getLikes(),comment.getCreatedDate()
        );
        Optional<Article> articleOptional = articleRepository.findById(comment.getArticleId());
        if (articleOptional.isPresent()) {
            articleOptional.get().setRecentCommentDate(new Timestamp(System.currentTimeMillis()));
            articleRepository.save(articleOptional.get());
            MessageComment messageComment = new MessageComment();
            messageComment.setCommentId(comment.getId());
            messageComment.setContent(comment.getContent());
            messageComment.setArticleId(articleOptional.get().getId());
            messageComment.setTitle(articleOptional.get().getTitle());
            messageComment.setFromUserId(user.getId());
            messageComment.setToUserId(articleOptional.get().getUserId());
            messageService.sendMessage(messageComment);
        }
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
                PageRequest.of(page-1, 5,Sort.by(Sort.Direction.DESC,"createdDate")));
        List<Comment> commentList = commentPage.toList();
        List<CommentDTO> all = new ArrayList<>();
        Long userId = userService.getUserId();
        for (Comment comment : commentList) {
            User user = userService.findById(comment.getUserId());
            Boolean isLike = false;
            if (userId != null) {
                isLike = likeRepository.
                        existsByTypeIdAndUserIdAndStatusAndType(comment.getId(), userId,true,Type.COMMENT);
            }
            SimpleCommentDTO simpleCommentDTO = new SimpleCommentDTO(
                    user.getId(),user.getNickname(), user.getHeadUrl(), comment.getId(), comment.getContent(),
                    isLike,comment.getLikes(),comment.getCreatedDate()
            );
            List<Reply> replyList = replyService.findAllByCommentId(comment.getId());
            List<SimpleReplyDTO> simpleReplyDTOS = new ArrayList<>();
            for (Reply reply : replyList) {
                User fromUser = userService.findById(reply.getFromUserId());
                User toUser = userService.findById(reply.getToUserId());
                SimpleReplyDTO simpleReplyDTO = new SimpleReplyDTO(
                        fromUser.getId(),fromUser.getNickname(),fromUser.getHeadUrl(),
                        toUser.getId(),toUser.getNickname(),
                        reply.getId(),reply.getContent(),reply.getLikes(),reply.getCreatedDate()
                );
                simpleReplyDTOS.add(simpleReplyDTO);
            }
            CommentDTO commentDTO = new CommentDTO(simpleCommentDTO, simpleReplyDTOS);
            all.add(commentDTO);
        }
        PageDTO<CommentDTO> comments = new PageDTO<>(all,commentPage.getTotalElements(),commentPage.getTotalPages());
        return ResultVoUtil.success(comments);
    }

    @Override
    public void like(Likes l) {
        Likes like = likeRepository.findByTypeIdAndUserIdAndType(l.getTypeId(),l.getUserId(), Type.COMMENT);
        if (like != null) {
            like.setStatus(!like.getStatus());
            if (like.getStatus()) {
                addLikeCount(like.getTypeId());
            } else  {
                delLikeCount(like.getTypeId());
            }
            likeRepository.save(like);
        } else {
            l.setStatus(true);
            l.setType(Type.COMMENT);
            addLikeCount(l.getTypeId());
            likeRepository.save(l);
            MessageLike messageLike = new MessageLike();
            messageLike.setTypeId(l.getTypeId());
            messageLike.setFromUserId(l.getUserId());
            Comment comment =  commentRepository.getOne(l.getTypeId());
            messageLike.setToUserId(comment.getUserId());
            messageLike.setMessageType(MessageType.COMMENT);
            messageService.sendMessage(messageLike);
        }
    }

    private void addLikeCount(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            optionalComment.get().setLikes(optionalComment.get().getLikes()+1);
            commentRepository.save(optionalComment.get());
        }
    }

    private void delLikeCount(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            optionalComment.get().setLikes(optionalComment.get().getLikes()-1);
            commentRepository.save(optionalComment.get());
        }
    }

}
