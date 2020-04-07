package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.SimpleReplyDTO;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.Type;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.LikeRepository;
import com.xzy.read.repository.ReplyRepository;
import com.xzy.read.service.MessageService;
import com.xzy.read.service.ReplyService;
import com.xzy.read.service.UserService;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * @author XieZhongYi
 * 2020/03/29 19:44
 */
@Service
public class ReplyServiceImpl implements ReplyService {

    private ReplyRepository replyRepository;

    private ArticleRepository articleRepository;

    private UserService userService;

    private LikeRepository likeRepository;

    private MessageService messageService;

    public ReplyServiceImpl(ReplyRepository replyRepository, ArticleRepository articleRepository, UserService userService, LikeRepository likeRepository, MessageService messageService) {
        this.replyRepository = replyRepository;
        this.articleRepository = articleRepository;
        this.userService = userService;
        this.likeRepository = likeRepository;
        this.messageService = messageService;
    }

    @Override
    public ResultVo reply(Reply reply) {
        reply = replyRepository.save(reply);
        User fromUser = userService.findById(reply.getFromUserId());
        User toUser = userService.findById(reply.getToUserId());
        SimpleReplyDTO simpleReplyDTO = new SimpleReplyDTO(
                fromUser.getId(),fromUser.getNickname(),fromUser.getHeadUrl(),
                toUser.getId(),toUser.getNickname(),
                reply.getId(),reply.getContent(),reply.getLikes(),reply.getCreatedDate()
        );
        Optional<Article> articleOptional = articleRepository.findById(reply.getArticleId());
        if (articleOptional.isPresent()) {
            articleOptional.get().setRecentCommentDate(new Timestamp(System.currentTimeMillis()));
            articleRepository.save(articleOptional.get());
            MessageComment messageComment = new MessageComment();
            messageComment.setCommentId(reply.getCommentId());
            messageComment.setContent(reply.getContent());
            messageComment.setArticleId(articleOptional.get().getId());
            messageComment.setTitle(articleOptional.get().getTitle());
            messageComment.setFromUserId(reply.getFromUserId());
            messageComment.setToUserId(reply.getToUserId());
            messageService.sendMessage(messageComment);
        }
        return ResultVoUtil.success(simpleReplyDTO);
    }

    @Override
    public ResultVo deleteReply(Reply reply) {
        replyRepository.deleteById(reply.getId());
        return ResultVoUtil.success();
    }

    @Override
    public List<Reply> findAllByCommentId(Long commentId) {
        return replyRepository.findAllByCommentId(commentId);
    }

    @Override
    public void like(Likes l) {
        Likes like = likeRepository.findByTypeIdAndUserIdAndType(l.getTypeId(),l.getUserId(), Type.REPLY);
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
            l.setType(Type.REPLY);
            addLikeCount(l.getTypeId());
            likeRepository.save(l);
        }
    }

    private void addLikeCount(Long id) {
        Optional<Reply> optionalReply = replyRepository.findById(id);
        if (optionalReply.isPresent()) {
            optionalReply.get().setLikes(optionalReply.get().getLikes()+1);
            replyRepository.save(optionalReply.get());
        }
    }

    private void delLikeCount(Long id) {
        Optional<Reply> optionalReply = replyRepository.findById(id);
        if (optionalReply.isPresent()) {
            optionalReply.get().setLikes(optionalReply.get().getLikes()-1);
            replyRepository.save(optionalReply.get());
        }
    }

}
