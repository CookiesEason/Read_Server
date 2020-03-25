package com.xzy.read.service;

import com.sun.xml.bind.v2.TODO;
import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Article;

/**
 * @author XieZhongYi
 * 2020/03/25 15:58
 */
public interface ArticleService {

    /**
     * TODO 推荐待完成
     */

    ResultVo findAllByNoteId(Long id);

    ResultVo create(Article article);

    ResultVo update(Article article);

    ResultVo delete(Article article);

    ResultVo setTop(Article article);

    ResultVo move(Article article);

}
