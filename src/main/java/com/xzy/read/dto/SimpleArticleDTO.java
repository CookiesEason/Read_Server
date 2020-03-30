package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/03/30 14:24
 */
@Data
@AllArgsConstructor
public class SimpleArticleDTO {

    private Long articleId;

    private String title;

    private String content;

    private Long userId;

    private String nickname;

    private Long comments;

    private Long likes;

}
