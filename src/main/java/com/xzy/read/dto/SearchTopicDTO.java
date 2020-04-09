package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/08 19:13
 */
@Data
@AllArgsConstructor
public class SearchTopicDTO {

    private Long id;

    private String topicHeadUrl;

    private String name;

    private Long articles;

    private Long followers;

    private Boolean isFollowed;

}
