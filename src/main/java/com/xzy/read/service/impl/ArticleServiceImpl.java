package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.ArticleDTO;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.Likes;
import com.xzy.read.entity.NoteBooks;
import com.xzy.read.entity.User;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.LikeRepository;
import com.xzy.read.service.*;
import com.xzy.read.util.ResultVoUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private UserService userService;

    private FileService fileService;

    private FollowersService followersService;

    private NoteBooksService noteBooksService;

    private LikeRepository likeRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository, UserService userService, FileService fileService, FollowersService followersService, NoteBooksService noteBooksService, LikeRepository likeRepository) {
        this.articleRepository = articleRepository;
        this.userService = userService;
        this.fileService = fileService;
        this.followersService = followersService;
        this.noteBooksService = noteBooksService;
        this.likeRepository = likeRepository;
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

    @Override
    public ResultVo findArticleById(Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (articleOptional.isPresent()) {
            Article article = articleOptional.get();
            User user = userService.findById(article.getUserId());
            Long userId = userService.getUserId();
            Long isFollowed = followersService.countByFromUserIdAndToUserIdAndStatus(
                    userId, article.getUserId(), true
            );
            NoteBooks noteBooks = noteBooksService.findById(article.getNotebookId());
            ArticleDTO articleDTO = new ArticleDTO(
                    user.getId(),user.getHeadUrl(),user.getNickname(),isFollowed > 0 ,article,noteBooks
            );
            return ResultVoUtil.success(articleDTO);
        }
        return ResultVoUtil.error(0,"该文章不存在");
    }

    @Override
    public ResultVo findSomeArticles(Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (articleOptional.isPresent()) {
            Article article = articleOptional.get();
            PageRequest pageRequest =
                    PageRequest.of(0,4, Sort.by(Sort.Direction.DESC, "createdDate"));
            List<Article> articles = articleRepository.findAllByUserIdAndIsPublished(article.getUserId(), true,pageRequest);
            return ResultVoUtil.success(articles);
        }
        return ResultVoUtil.error(0, "该文章不存在");
    }

    @Override
    public void like(Likes l) {
        Likes like = likeRepository.findByArticleIdAndUserId(l.getArticleId(),l.getUserId());
        if (like != null) {
            like.setStatus(!like.getStatus());
            if (like.getStatus()) {
                addLikeCount(like.getArticleId());
            } else  {
                delLikeCount(like.getArticleId());
            }
            likeRepository.save(like);
        } else {
            l.setStatus(true);
            addLikeCount(l.getArticleId());
            likeRepository.save(l);
        }
    }

    @Override
    public void addClickCount(Article article) {
        Optional<Article> articleOptional = articleRepository.findById(article.getId());
        if (articleOptional.isPresent()) {
            articleOptional.get().setClicks(articleOptional.get().getClicks()+1);
            articleRepository.save(articleOptional.get());
        }
    }

    private void addLikeCount(Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (articleOptional.isPresent()) {
            articleOptional.get().setLikes(articleOptional.get().getLikes()+1);
            articleRepository.save(articleOptional.get());
        }
    }

    private void delLikeCount(Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (articleOptional.isPresent()) {
            articleOptional.get().setLikes(articleOptional.get().getLikes()-1);
            articleRepository.save(articleOptional.get());
        }
    }

    private String removeHtml (String content) {
        if (content == null) {
            return "";
        }
        Document doc = Jsoup.parse(content);
        return doc.text();
    }
}
