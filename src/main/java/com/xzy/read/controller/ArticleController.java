package com.xzy.read.controller;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.NoteBooks;
import com.xzy.read.service.ArticleService;
import org.springframework.web.bind.annotation.*;

/**
 * @author XieZhongYi
 * 2020/03/25 16:18
 */
@RestController
@RequestMapping("/api")
public class ArticleController {

    private ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/articles")
    public ResultVo findAllByNoteId(Long noteId) {
        return articleService.findAllByNoteId(noteId);
    }

    @PostMapping("/articles")
    public ResultVo create(@RequestBody Article article) {
        return articleService.create(article);
    }

    @PutMapping("/articles")
    public ResultVo update(@RequestBody Article article) {
        return articleService.update(article);
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

}
