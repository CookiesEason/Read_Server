package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XieZhongYi
 * 2020/04/08 10:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestTopicArticle {

    private Long id;

    private Long articleId;

    private Long topicId;

    private Boolean isPassed;

    private Long userId;

    private String reason;

}
