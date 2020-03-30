package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.SimpleReplyDTO;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.Reply;
import com.xzy.read.entity.User;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.ReplyRepository;
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

    public ReplyServiceImpl(ReplyRepository replyRepository, ArticleRepository articleRepository, UserService userService) {
        this.replyRepository = replyRepository;
        this.articleRepository = articleRepository;
        this.userService = userService;
    }

    @Override
    public ResultVo reply(Reply reply) {
        reply = replyRepository.save(reply);
        User fromUser = userService.findById(reply.getFromUserId());
        User toUser = userService.findById(reply.getToUserId());
        SimpleReplyDTO simpleReplyDTO = new SimpleReplyDTO(
                fromUser.getId(),fromUser.getNickname(),fromUser.getHeadUrl(),
                toUser.getId(),toUser.getNickname(),
                reply.getContent(),reply.getCreatedDate()
        );
        Optional<Article> articleOptional = articleRepository.findById(reply.getArticleId());
        if (articleOptional.isPresent()) {
            articleOptional.get().setRecentCommentDate(new Timestamp(System.currentTimeMillis()));
            articleRepository.save(articleOptional.get());
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
}
