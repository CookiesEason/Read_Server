package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/07 13:52
 */
@Data
@AllArgsConstructor
public class MessageLikeDTO {

    private Long userId;

    private String headUrl;

    private String nickname;

    private Long articleId;

    private String title;

    private String comment;

    private Timestamp date;

    private Boolean isComment;

}
