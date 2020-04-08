package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/04 11:27
 */
@Data
@AllArgsConstructor
public class MessageArticleDTO {

    private Long userId;

    private String headUrl;

    private String nickname;

    private Timestamp createdDate;

    private Long articleId;

    private String title;

    private String content;

    private Long clicks;

    private Long comments;

    private Long likes;

    private Boolean isPassed;

    private Long topicId;

}
