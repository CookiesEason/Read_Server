package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/06 12:07
 */
@Data
@AllArgsConstructor
public class FollowDTO {

    private Long typeId;

    private String name;

    private String headUrl;

    private Boolean isNb;

}
