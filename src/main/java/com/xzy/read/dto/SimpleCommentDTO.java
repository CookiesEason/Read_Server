package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/03/29 18:56
 */
@Data
@AllArgsConstructor
public class SimpleCommentDTO {

    private Long userId;

    private String nickname;

    private String headUrl;

    private String content;

    private Timestamp createDate;

}
