package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.*;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.MessageType;
import com.xzy.read.repository.*;
import com.xzy.read.service.MessageService;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/07 10:20
 */
@Service
public class MessageServiceImpl implements MessageService {


    private MessageCommentRepository messageCommentRepository;

    private MessageLikeRepository messageLikeRepository;

    private MessageFollowRepository messageFollowRepository;

    private UserRepository userRepository;

    private ArticleRepository articleRepository;

    private CommentRepository commentRepository;

    private NoteBooksRepository noteBooksRepository;

    private TopicRepository topicRepository;

    private FollowersRepository followersRepository;

    public MessageServiceImpl(MessageCommentRepository messageCommentRepository, MessageLikeRepository messageLikeRepository, MessageFollowRepository messageFollowRepository, UserRepository userRepository, ArticleRepository articleRepository, CommentRepository commentRepository, NoteBooksRepository noteBooksRepository, TopicRepository topicRepository, FollowersRepository followersRepository) {
        this.messageCommentRepository = messageCommentRepository;
        this.messageLikeRepository = messageLikeRepository;
        this.messageFollowRepository = messageFollowRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.noteBooksRepository = noteBooksRepository;
        this.topicRepository = topicRepository;
        this.followersRepository = followersRepository;
    }

    @Override
    public ResultVo countUnreadMessage(Long userId) {
        MessageDTO messageDTO = new MessageDTO(
                messageCommentRepository.countUnreadMessage(userId),
                messageLikeRepository.countUnreadMessage(userId),
                0L,
                messageFollowRepository.countUnreadMessage(userId)
        );
        return ResultVoUtil.success(messageDTO);
    }

    @Override
    public ResultVo getCommentMessages(Long userId, int page) {
        Page<MessageComment> commentPage = messageCommentRepository.
                findAllByToUserId(userId, PageRequest.of(page - 1, 5,
                        Sort.by(Sort.Direction.DESC, "id","isRead")));
        List<MessageCommentDTO> commentDTOList = new ArrayList<>();
        for (MessageComment messageComment : commentPage.toList()) {
            User user = userRepository.getOne(messageComment.getFromUserId());
            MessageCommentDTO messageCommentDTO = new MessageCommentDTO(
                    user.getId(), user.getHeadUrl(), user.getNickname(),
                    messageComment.getArticleId(), messageComment.getTitle(),
                    messageComment.getCommentId(),
                    messageComment.getContent(), messageComment.getCreatedDate()
            );
            commentDTOList.add(messageCommentDTO);
        }
        PageDTO<MessageCommentDTO> pageDTO = new PageDTO<>(commentDTOList,
                commentPage.getTotalElements(), commentPage.getTotalPages());
        readMessage(userId, 0);
        return ResultVoUtil.success(pageDTO);
    }

    @Override
    public ResultVo getLikeMessages(Long userId, int page) {
        Page<MessageLike> likePage = messageLikeRepository.findAllByToUserId(userId,
                PageRequest.of(page - 1, 5,
                Sort.by(Sort.Direction.DESC, "id","isRead")));
        List<MessageLikeDTO> likeDTOS = new ArrayList<>();
        for (MessageLike messageLike : likePage.toList()) {
            User user = userRepository.getOne(messageLike.getFromUserId());
            if (messageLike.getMessageType().equals(MessageType.ARTICLE)) {
                Article article = articleRepository.getOne(messageLike.getTypeId());
                MessageLikeDTO messageLikeDTO = new MessageLikeDTO(
                        user.getId(), user.getHeadUrl(), user.getNickname(),
                        messageLike.getTypeId(),article.getTitle(),null,
                        messageLike.getCreatedDate(),false);
                likeDTOS.add(messageLikeDTO);
            } else {
                Comment comment = commentRepository.getOne(messageLike.getTypeId());
                MessageLikeDTO messageLikeDTO = new MessageLikeDTO(
                        user.getId(), user.getHeadUrl(), user.getNickname(),
                        comment.getArticleId(),null,comment.getContent(),
                        messageLike.getCreatedDate(),true);
                likeDTOS.add(messageLikeDTO);
            }
        }
        PageDTO<MessageLikeDTO> pageDTO = new PageDTO<>(likeDTOS,
                likePage.getTotalElements(), likePage.getTotalPages());
        readMessage(userId, 1);
        return ResultVoUtil.success(pageDTO);
    }

    @Override
    public ResultVo getFollowMessages(Long userId, int page) {
        Page<MessageFollow> followPage = messageFollowRepository.findAllByToUserId(userId,
                PageRequest.of(page - 1, 5,
                        Sort.by(Sort.Direction.DESC, "id","isRead")));
        List<MessageFollowDTO> messageFollowDTOS = new ArrayList<>();
        for (MessageFollow messageFollow : followPage.toList()) {
            User user = userRepository.getOne(messageFollow.getFromUserId());
            if (messageFollow.getMessageType().equals(MessageType.NOTEBOOK)) {
                NoteBooks noteBooks = noteBooksRepository.getOne(messageFollow.getTypeId());
                MessageFollowDTO messageFollowDTO = new MessageFollowDTO(
                        user.getId(), user.getHeadUrl(), user.getNickname(),
                        noteBooks.getId(), noteBooks.getName(), false,
                        messageFollow.getCreatedDate(), 3
                );
                messageFollowDTOS.add(messageFollowDTO);
            } else  if (messageFollow.getMessageType().equals(MessageType.TOPIC)) {
                Topic topic = topicRepository.getOne(messageFollow.getTypeId());
                MessageFollowDTO messageFollowDTO = new MessageFollowDTO(
                        user.getId(), user.getHeadUrl(), user.getNickname(),
                        topic.getId(), topic.getName(), false,
                        messageFollow.getCreatedDate(), 2
                );
                messageFollowDTOS.add(messageFollowDTO);
            } else {
                MessageFollowDTO messageFollowDTO = new MessageFollowDTO(
                        user.getId(), user.getHeadUrl(), user.getNickname(),
                        null, null,
                        followersRepository.countByFromUserIdAndToUserIdAndStatus(messageFollow.getFromUserId(),
                                messageFollow.getToUserId(), true) > 0,
                        messageFollow.getCreatedDate(), 1
                );
                messageFollowDTOS.add(messageFollowDTO);
            }
        }
        PageDTO<MessageFollowDTO> pageDTO = new PageDTO<>(messageFollowDTOS,
                followPage.getTotalElements(), followPage.getTotalPages());
        readMessage(userId, 3);
        return ResultVoUtil.success(pageDTO);
    }

    private void readMessage(Long userId, int type) {
        if (type == 0) {
            List<MessageComment> messageComments = messageCommentRepository.findAllByToUserIdAndIsRead(userId, false);
            messageComments.forEach(messageComment -> messageComment.setIsRead(true));
            messageCommentRepository.saveAll(messageComments);
        } else if (type == 1) {
            List<MessageLike> messageLikes = messageLikeRepository.findAllByToUserIdAndIsRead(userId, false);
            messageLikes.forEach(messageLike -> messageLike.setIsRead(true));
            messageLikeRepository.saveAll(messageLikes);
        } else if (type == 2){
        } else {
            List<MessageFollow> messageFollows = messageFollowRepository.findAllByToUserIdAndIsRead(userId, false);
            messageFollows.forEach(messageFollow -> messageFollow.setIsRead(true));
            messageFollowRepository.saveAll(messageFollows);
        }

    }

    @Override
    public void sendMessage(MessageComment messageComment) {
        messageCommentRepository.save(messageComment);
    }

    @Override
    public void sendMessage(MessageFollow messageFollow) {
        messageFollowRepository.save(messageFollow);
    }

    @Override
    public void sendMessage(MessageLike messageLike) {
        messageLikeRepository.save(messageLike);
    }
}
