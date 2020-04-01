package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/01 17:39
 */
@Data
@AllArgsConstructor
public class TopicDTO {

    private Long id;

    private String topicHeadUrl;

    private String name;

    private String introduce;

    private Long userId;

    private String userHeadUrl;

    private String nickname;

    private Long articles;

    private Long followers;

    private Boolean isFollowed;

}
