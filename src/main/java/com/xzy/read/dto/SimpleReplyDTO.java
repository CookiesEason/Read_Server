package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/03/29 19:49
 */
@Data
@AllArgsConstructor
public class SimpleReplyDTO {

    private Long userId;

    private String nickname;

    private String headUrl;

    private Long toUserId;

    private String toNickname;

    private String content;

    private Timestamp createDate;

}
