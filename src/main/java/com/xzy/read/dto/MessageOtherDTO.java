package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/07 21:37
 */
@AllArgsConstructor
@Data
public class MessageOtherDTO {

    private Long articleId;

    private String title;

    private Long topicId;

    private String name;

    private String reason;

    private Boolean isRejected;

    private Timestamp data;

}
