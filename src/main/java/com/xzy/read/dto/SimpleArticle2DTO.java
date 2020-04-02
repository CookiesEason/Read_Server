package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/02 13:48
 */
@Data
@AllArgsConstructor
public class SimpleArticle2DTO {

    private Long articleId;

    private String title;

    private Boolean isCollected;

}
