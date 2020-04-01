package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/01 10:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendUserDTO {

    private BigInteger id;

    private String headUrl;

    private String nickname;

    private String introduce;

    private String sex;

    private BigDecimal words;

    private BigDecimal  likes;

    private Boolean isFollowed;

    List<RecommendSimpleArticle> simpleArticleDTOS;
}
