package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/04 19:20
 */
@Data
@AllArgsConstructor
public class FollowUserDTO {

    private Long userId;

    private String headUrl;

    private String nickname;

    private Long followers;

    private Long fans;

    private Long articles;

    private Long words;

    private Long likes;

    private Boolean isFollowed;

}
