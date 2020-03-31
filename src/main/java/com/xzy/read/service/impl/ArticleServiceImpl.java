package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.ArticleDTO;
import com.xzy.read.dto.LikeUserDTO;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.dto.SimpleArticleDTO;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.Type;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.CollectionRepository;
import com.xzy.read.repository.LikeRepository;
import com.xzy.read.service.*;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    private CollectionRepository collectionRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository, UserService userService, FileService fileService, FollowersService followersService, NoteBooksService noteBooksService, LikeRepository likeRepository, CollectionRepository collectionRepository) {
        this.articleRepository = articleRepository;
        this.userService = userService;
        this.fileService = fileService;
        this.followersService = followersService;
        this.noteBooksService = noteBooksService;
        this.likeRepository = likeRepository;
        this.collectionRepository = collectionRepository;
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
        Long userId = userService.getUserId();
        if (articleOptional.isPresent()) {
            boolean isLiked = false, isCollected = false, isFollowed = false;
            Article article = articleOptional.get();
            User user = userService.findById(article.getUserId());
            if (userId != null ) {
                isFollowed = followersService.countByFromUserIdAndToUserIdAndStatus(
                        userId, article.getUserId(), true
                ) > 0;
                isLiked = likeRepository.existsByTypeIdAndUserIdAndStatusAndType(article.getId(), userId, true, Type.ARTICLE);
                isCollected = collectionRepository.existsByArticleIdAndUserId(article.getId(),userId);
            }
            NoteBooks noteBooks = noteBooksService.findById(article.getNotebookId());
            Long allLikes = articleRepository.countLikesByUserId(article.getUserId());
            Long allWords = articleRepository.countWordsByUserId(article.getUserId());
            Long fans = followersService.countFans(article.getUserId());
            ArticleDTO articleDTO = new ArticleDTO(
                    user.getId(),user.getHeadUrl(),user.getNickname(),user.getIntroduce()
                    ,isFollowed,article,
                    isLiked, isCollected, noteBooks.getId(), noteBooks.getName(),
                    allLikes, allWords, fans
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
        Likes like = likeRepository.findByTypeIdAndUserIdAndType(l.getTypeId(),l.getUserId(), Type.ARTICLE);
        if (like != null) {
            like.setStatus(!like.getStatus());
            if (like.getStatus()) {
                addLikeCount(like.getTypeId());
            } else  {
                delLikeCount(like.getTypeId());
            }
            likeRepository.save(like);
        } else {
            l.setStatus(true);
            l.setType(Type.ARTICLE);
            addLikeCount(l.getTypeId());
            likeRepository.save(l);
        }
    }

    @Override
    public ResultVo likesUsers(Long articleId, int page) {
        Long userId = userService.getUserId();
        Page<Likes> likesPage =  likeRepository.findAllByTypeIdAndStatusAndType(articleId,true,
                    PageRequest.of(page-1,10,Sort.by(Sort.Direction.DESC, "id")),Type.ARTICLE);
        List<Likes> likes = likesPage.toList();
        List<LikeUserDTO> likeUserDTOS = new ArrayList<>();
        for (Likes like : likes) {
            User user = userService.findById(like.getUserId());
            boolean isFollowed = false;
            if ( userId != null) {
                isFollowed = followersService.countByFromUserIdAndToUserIdAndStatus(
                        like.getUserId(), userId, true
                ) > 0;
            }
            LikeUserDTO likeUserDTO = new LikeUserDTO(user.getId(), user.getNickname(), user.getHeadUrl(),isFollowed);
            likeUserDTOS.add(likeUserDTO);
        }
        PageDTO<LikeUserDTO> pageDTO = new PageDTO<>(likeUserDTOS, likesPage.getTotalElements(), likesPage.getTotalPages());
        return ResultVoUtil.success(pageDTO);
    }

    @Override
    public void addClickCount(Article article) {
        Optional<Article> articleOptional = articleRepository.findById(article.getId());
        if (articleOptional.isPresent()) {
            articleOptional.get().setClicks(articleOptional.get().getClicks()+1);
            articleRepository.save(articleOptional.get());
        }
    }

    @Override
    public ResultVo collection(Collection collection) {
        collectionRepository.save(collection);
        return ResultVoUtil.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo cancelCollection(Collection collection) {
        collectionRepository.deleteByArticleIdAndUserId(collection.getArticleId(), collection.getUserId());
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo findCollections() {
        User user = userService.findByTelephone(SecurityUtil.getAuthentication().getName());
        List<Collection> collections = collectionRepository.findAllByUserId(user.getId());
        List<Long> ids = new ArrayList<>();
        for (Collection collection : collections) {
            ids.add(collection.getArticleId());
        }
        List<Article> articleList = articleRepository.findAllByIdIn(ids);
        List<SimpleArticleDTO> articles = new ArrayList<>();
        for (Article article : articleList) {
            User articleUser = userService.findById(article.getUserId());
            SimpleArticleDTO simpleArticleDTO = new SimpleArticleDTO(
                    article.getId(),article.getTitle(),removeHtml(article.getContent()),
                    articleUser.getId(),articleUser.getNickname(),
                    articleRepository.countCommentsByArticleId(article.getId()),article.getLikes()
            );
            articles.add(simpleArticleDTO);
        }
        return ResultVoUtil.success(articles);
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
