package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/03 11:36
 */
@Data
@AllArgsConstructor
public class SimpleArticleNoteBookDTO {

    private Long articleId;

    private String title;

    private String content;

    private Long clicks;

    private Long comments;

    private Long likes;

    private Timestamp createdDate;

    private Boolean isTop;

}
