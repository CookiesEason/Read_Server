package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/01 19:46
 */
@Data
@AllArgsConstructor
public class FollowerDTO {

    private Long userId;

    private String headUrl;

    private String nickname;

    private Timestamp createdDate;

}
