package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/07 14:07
 */
@AllArgsConstructor
@Data
public class MessageFollowDTO {

    private Long userId;

    private String headUrl;

    private String nickname;

    private Long typeId;

    private String name;

    private Boolean isFollowed;

    private Timestamp date;

    /**
     * 1: 用户 2: 专题 3：文集
     */
    private Integer type;

}
