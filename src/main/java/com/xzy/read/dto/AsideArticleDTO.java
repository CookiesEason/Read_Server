package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/14 16:29
 */
@Data
@AllArgsConstructor
public class AsideArticleDTO {

    private Long articleId;

    private String title;

    private Long clicks;

}
