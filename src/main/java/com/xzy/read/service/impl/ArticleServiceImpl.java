package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Article;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.service.ArticleService;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author XieZhongYi
 * 2020/03/25 16:00
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public ResultVo findAllByNoteId(Long id) {
        //TODO 字段应不显示文章的内容
        List<Article> articles = articleRepository.findAllByNotebookIdAndIsDeleted(id,false);
        return ResultVoUtil.success(articles);
    }

    @Override
    public ResultVo create(Article article) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        article.setTitle(date.format(formatter));
        articleRepository.save(article);
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo update(Article article) {
        Optional<Article> articleOptional = articleRepository.findById(article.getId());
        if (articleOptional.isPresent()) {
            Article article1 = articleOptional.get();
            article1.setTitle(article.getTitle());
            article1.setContent(article.getContent());
            article1.setWords(article.getWords());
            articleRepository.save(article1);
            return ResultVoUtil.success();
        }
        return ResultVoUtil.error(0,"该文章不存在");
    }

    /**
     * 伪删除
     * @param article
     * @return
     */
    @Override
    public ResultVo delete(Article article) {
        Optional<Article> articleOptional = articleRepository.findById(article.getId());
        if (articleOptional.isPresent()) {
            articleOptional.get().setIsDeleted(true);
            articleRepository.save(articleOptional.get());
            return ResultVoUtil.success();
        }
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo setTop(Article article) {
        Optional<Article> articleOptional = articleRepository.findById(article.getId());
        if (articleOptional.isPresent()) {
            if (!article.getIsTop()) {
                Article old =  articleRepository.findByIsTop(true);
                if (old!=null) {
                    old.setIsTop(false);
                    articleRepository.save(old);
                }
            }
            articleOptional.get().setIsTop(!article.getIsTop());
            articleRepository.save(articleOptional.get());
            return ResultVoUtil.success();
        }
        return ResultVoUtil.error(0,"该文章不存在");
    }

    @Override
    public ResultVo move(Article article) {
        Optional<Article> articleOptional = articleRepository.findById(article.getId());
        if (articleOptional.isPresent()) {
            articleOptional.get().setNotebookId(article.getNotebookId());
            articleRepository.save(articleOptional.get());
            return ResultVoUtil.success();
        }
        return ResultVoUtil.error(0,"该文章不存在");
    }
}
