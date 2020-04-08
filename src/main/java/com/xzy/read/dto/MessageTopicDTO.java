package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/07 20:23
 */
@AllArgsConstructor
@Data
public class MessageTopicDTO {

    private Long topicId;

    private String name;

    private String headUrl;

    private Long unSolve;

}
