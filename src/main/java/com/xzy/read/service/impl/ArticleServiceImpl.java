package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Article;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.service.ArticleService;
import com.xzy.read.service.FileService;
import com.xzy.read.service.UserService;
import com.xzy.read.util.ResultVoUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XieZhongYi
 * 2020/03/25 16:00
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;

    private UserService userService;

    private FileService fileService;

    public ArticleServiceImpl(ArticleRepository articleRepository, UserService userService, FileService fileService) {
        this.articleRepository = articleRepository;
        this.userService = userService;
        this.fileService = fileService;
    }

    @Override
    public ResultVo findAllByNoteId(Long id) {
        //TODO 字段应不显示文章的内容
        List<Article> articles = articleRepository.findAllByNotebookIdAndIsDeleted(id,false);
        for (Article article : articles) {
            article.setContent(removeHtml(article.getContent()));
        }
        return ResultVoUtil.success(articles);
    }

    @Override
    public ResultVo create(Article article) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        article.setTitle(date.format(formatter));
        article.setUserId(userService.getUserId());
        articleRepository.save(article);
        return ResultVoUtil.success(article);
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
            article1.setContent(removeHtml(article1.getContent()));
            return ResultVoUtil.success(article1);
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
        return ResultVoUtil.error(0,"文章不存在");
    }

    @Override
    public ResultVo deleteData(Article article) {
        articleRepository.deleteById(article.getId());
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo getRecycleData() {
        Long id = userService.getUserId();
        List<Article> articles = articleRepository.findAllByIsDeletedAndUserId(true, id);
        return ResultVoUtil.success(articles);
    }

    @Override
    public ResultVo recycle(Article article) {
        Optional<Article> articleOptional = articleRepository.findById(article.getId());
        if (articleOptional.isPresent()) {
            articleOptional.get().setIsDeleted(false);
            articleRepository.save(articleOptional.get());
            return ResultVoUtil.success();
        }
        return ResultVoUtil.error(0,"文章不存在");
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

    @Override
    public ResultVo publish(Article article) {
        Optional<Article> articleOptional = articleRepository.findById(article.getId());
        if (articleOptional.isPresent()) {
            Article article1 = articleOptional.get();
            article1.setIsPublished(true);
            article1.setContent(article.getContent());
            article1.setTitle(article.getTitle());
            article1.setWords(article.getWords());
            articleRepository.save(article1);
            article1.setContent(removeHtml(article1.getContent()));
            return ResultVoUtil.success(article1);
        }
        return ResultVoUtil.error(0,"该文章不存在");
    }

    @Override
    public ResultVo uploadImg(MultipartFile multipartFile) {
        return fileService.uploadFile(multipartFile);
    }

    @Override
    public ResultVo findById(Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        return articleOptional.map(ResultVoUtil::success).orElseGet(() -> ResultVoUtil.error(0, "该文章不存在"));
    }

    private String removeHtml (String content) {
        if (content == null) {
            return "";
        }
        Document doc = Jsoup.parse(content);
        return doc.text();
    }
}
