package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/04 10:56
 */
@Data
@AllArgsConstructor
public class UserInfoDTO {

    private Long userId;

    private String headUrl;

    private String nickname;

    private String introduce;

    private Long followers;

    private Long fans;

    private Long articles;

    private Long words;

    private Long likes;

    private Boolean isFollowed;

}
