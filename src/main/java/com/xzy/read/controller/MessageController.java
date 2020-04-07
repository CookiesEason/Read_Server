package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.service.MessageService;
import org.springframework.web.bind.annotation.*;

/**
 * @author XieZhongYi
 * 2020/04/07 11:32
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
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


}
