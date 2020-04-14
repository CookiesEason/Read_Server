package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.Collection;
import com.xzy.read.entity.Likes;
import com.xzy.read.service.ArticleService;
import com.xzy.read.service.RecommendService;
import com.xzy.read.service.TopicService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author XieZhongYi
 * 2020/03/25 16:18
 */
@RestController
@RequestMapping("/api")
public class ArticleController {

    private ArticleService articleService;

    private TopicService topicService;

    private RecommendService recommendService;

    public ArticleController(ArticleService articleService, TopicService topicService, RecommendService recommendService) {
        this.articleService = articleService;
        this.topicService = topicService;
        this.recommendService = recommendService;
    }

    @GetMapping("/articles")
    public ResultVo findAllByNoteId(Long noteId) {
        return articleService.findAllByNoteId(noteId);
    }

    @GetMapping("/articles/one")
    public ResultVo findByArticleId(Long id) {
        return articleService.findById(id);
    }

    @GetMapping("/articles/topic")
    public ResultVo findTopicByArticleId(Long articleId, @RequestParam(defaultValue = "1") int page) {
        return topicService.findTopicsByArticleId(articleId, page);
    }

    @PostMapping("/articles")
    public ResultVo create(@RequestBody Article article) {
        return articleService.create(article);
    }

    @PutMapping("/articles")
    public ResultVo update(@RequestBody Article article) {
        return articleService.update(article);
    }

    @PutMapping("/articles/publish")
    public ResultVo publish(@RequestBody Article article) {
        return articleService.publish(article);
    }

    @PutMapping("/articles/top")
    public ResultVo setTop(@RequestBody Article article) {
        return articleService.setTop(article);
    }

    @PutMapping("/articles/move")
    public ResultVo move(@RequestBody Article article) {
        return articleService.move(article);
    }

    @DeleteMapping("/articles")
    public ResultVo delete(@RequestBody Article article) {
        return articleService.delete(article);
    }

    @GetMapping("/articles/recycle")
    public ResultVo recycle() {
        return articleService.getRecycleData();
    }

    @PutMapping("/articles/recycle")
    public ResultVo recycle(@RequestBody Article article) {
        return articleService.recycle(article);
    }

    @DeleteMapping("/articles/recycle")
    public ResultVo deleteData(@RequestBody Article article) {
        return articleService.deleteData(article);
    }

    @PostMapping("/articles/img")
    public ResultVo uploadImg(@RequestParam("file") MultipartFile multipartFile) {
        return articleService.uploadImg(multipartFile);
    }

    @GetMapping("/p/{id}")
    public ResultVo getArticle(@PathVariable Long id) {
        return articleService.findArticleById(id);
    }

    @GetMapping("/p/hot")
    public ResultVo getHotArticles(@RequestParam(defaultValue = "1") int page) {
        return articleService.findHotArticles(page);
    }

    @GetMapping("/p/user/{id}")
    public ResultVo getUserArticle(@PathVariable Long id) {
        return articleService.findSomeArticles(id);
    }

    @PutMapping("/p/like")
    public void like(@RequestBody Likes like) {
        articleService.like(like);
    }

    @GetMapping("/p/like/user")
    public ResultVo likeUsers(Long articleId, @RequestParam(defaultValue = "1") int page) {
        return articleService.likesUsers(articleId, page);
    }

    @PutMapping("/p/click")
    public void click(@RequestBody Article article) {
        articleService.addClickCount(article);
    }

    @PostMapping("/p/collection")
    public ResultVo collection(@RequestBody Collection collection) {
       return articleService.collection(collection);
    }

    @DeleteMapping("/p/collection")
    public ResultVo cancelCollection(@RequestBody Collection collection) {
        return articleService.cancelCollection(collection);
    }

    @GetMapping("/p/collection")
    public ResultVo collections() {
        return articleService.findCollections();
    }

    @GetMapping("/p/{id}/recommend")
    public ResultVo recommendById(@PathVariable Long id) {
        return articleService.recommendArticles(id);
    }

    @GetMapping("/articles/recommend")
    public ResultVo recommendByUserId(Long userId,@RequestParam(defaultValue = "1") int page) {
        return recommendService.recommendByUserId(userId, page);
    }

    @GetMapping("/articles/aside/recommend")
    public ResultVo recommendAsideArticles() {
        return articleService.findNewArticles();
    }

}
