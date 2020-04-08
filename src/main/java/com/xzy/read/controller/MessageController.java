package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.RequestTopicArticle;
import com.xzy.read.service.MessageService;
import com.xzy.read.service.TopicService;
import org.springframework.web.bind.annotation.*;

/**
 * @author XieZhongYi
 * 2020/04/07 11:32
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private MessageService messageService;

    private TopicService topicService;

    public MessageController(MessageService messageService, TopicService topicService) {
        this.messageService = messageService;
        this.topicService = topicService;
    }

    @GetMapping("/count")
    public ResultVo getMessageNum(Long userId) {
        return messageService.countUnreadMessage(userId);
    }

    @GetMapping("/comment")
    public ResultVo getMessageComment(Long userId, @RequestParam(defaultValue = "1") int page) {
        return messageService.getCommentMessages(userId, page);
    }

    @GetMapping("/like")
    public ResultVo getMessageLike(Long userId, @RequestParam(defaultValue = "1") int page) {
        return messageService.getLikeMessages(userId, page);
    }

    @GetMapping("/follow")
    public ResultVo getMessageFollow(Long userId, @RequestParam(defaultValue = "1") int page) {
        return messageService.getFollowMessages(userId, page);
    }

    @GetMapping("/others")
    public ResultVo getMessageOthers(Long userId, @RequestParam(defaultValue = "1") int page) {
        return messageService.getOthersMessages(userId, page);
    }

    @GetMapping("/topic")
    public ResultVo allTopics(Long userId) {
        return topicService.needSubmitTopic(userId);
    }

    @GetMapping("/topic/allRequests")
    public ResultVo allRequests(Long userId) {
        return topicService.allSubmitList(userId);
    }

    @GetMapping("/topic/{id}/allRequests")
    public ResultVo topicRequest(@PathVariable Long id,@RequestParam(defaultValue = "true") Boolean up) {
        return topicService.submitList(id, up);
    }

    @PutMapping("/topic/article")
    public void verify(@RequestBody RequestTopicArticle requestTopicArticle) {
         topicService.verify(requestTopicArticle);
    }


}
