package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/03/30 15:44
 */
@Data
@AllArgsConstructor
public class LikeUserDTO {

    private Long userId;

    private String nickname;

    private String headUrl;

    private Boolean isFollowed;

}
