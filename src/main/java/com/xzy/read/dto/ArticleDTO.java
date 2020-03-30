package com.xzy.read.dto;

import com.xzy.read.entity.Article;
import com.xzy.read.entity.NoteBooks;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @author XieZhongYi
 * 2020/03/28 21:21
 */
@Data
@AllArgsConstructor
public class ArticleDTO {

    private Long userId;

    private String headUrl;

    private String nickname;

    private Boolean isFollowed;

    private Article article;

    private Boolean isLiked;

    private Boolean isCollected;

    private Long nbId;

    private String notebook;

}
