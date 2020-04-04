package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/04 13:41
 */
@Data
@AllArgsConstructor
public class FollowWorkDTO {

    private Long typeId;

    private String name;

    private String headUrl;

    private Long articles;

    private Long followers;

    private Boolean isFollowed;

    private Boolean isNotebook;

}
