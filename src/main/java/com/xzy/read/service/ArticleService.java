package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.Likes;
import org.springframework.web.multipart.MultipartFile;

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

    ResultVo deleteData(Article article);

    ResultVo getRecycleData();

    ResultVo recycle(Article article);

    ResultVo setTop(Article article);

    ResultVo move(Article article);

    ResultVo publish(Article article);

    ResultVo uploadImg(MultipartFile multipartFile);

    ResultVo findById(Long id);

    ResultVo findArticleById(Long id);

    ResultVo findSomeArticles(Long id);

    void like(Likes like);

    void addClickCount(Article article);



}
