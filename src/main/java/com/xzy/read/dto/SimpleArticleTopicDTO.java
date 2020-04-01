package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/01 14:46
 */
@Data
@AllArgsConstructor
public class SimpleArticleTopicDTO {

    private Long id;

    private String headUrl;

    private String name;

    private Boolean isCollected;

}
