package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.*;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.MessageType;
import com.xzy.read.entity.enums.Type;
import com.xzy.read.repository.*;
import com.xzy.read.service.*;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import com.xzy.read.util.algorithm.TFIDF;
import lombok.extern.slf4j.Slf4j;
import org.ansj.app.keyword.Keyword;
import org.elasticsearch.index.query.QueryBuilders;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author XieZhongYi
 * 2020/03/25 16:00
 */
@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;

    private UserService userService;

    private FileService fileService;

    private FollowService followersService;

    private NoteBooksService noteBooksService;

    private LikeRepository likeRepository;

    private CollectionRepository collectionRepository;

    private TopicArticleRepository topicArticleRepository;

    private TimelineRepository timelineRepository;

    private MessageService messageService;

    private SearchArticleRepository searchArticleRepository;

    private ViewLogsRepository viewLogsRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository, UserService userService, FileService fileService, FollowService followersService, NoteBooksService noteBooksService, LikeRepository likeRepository, CollectionRepository collectionRepository, TopicArticleRepository topicArticleRepository, TimelineRepository timelineRepository, MessageService messageService, ElasticsearchRestTemplate elasticsearchRestTemplate, SearchArticleRepository searchArticleRepository, ViewLogsRepository viewLogsRepository) {
        this.articleRepository = articleRepository;
        this.userService = userService;
        this.fileService = fileService;
        this.followersService = followersService;
        this.noteBooksService = noteBooksService;
        this.likeRepository = likeRepository;
        this.collectionRepository = collectionRepository;
        this.topicArticleRepository = topicArticleRepository;
        this.timelineRepository = timelineRepository;
        this.messageService = messageService;
        this.searchArticleRepository = searchArticleRepository;
        this.viewLogsRepository = viewLogsRepository;
    }

    @Override
    public ResultVo recommendArticles(Long articleId) {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            List<Keyword> keywords = TFIDF.getTFIDE(article.getTitle(),removeHtml(article.getContent()),2);
            String key1 = keywords.get(0).getName();
            String key2 = keywords.get(1).getName();
            log.info("关键词" + keywords);
            SearchQuery searchQuery = new NativeSearchQueryBuilder().
                    withQuery(QueryBuilders.boolQuery().should(QueryBuilders.termQuery("title",key1))
                            .mustNot(QueryBuilders.termQuery("id", articleId))
                    .should(QueryBuilders.termQuery("content",key1).boost(2.0f))
                    .should(QueryBuilders.termQuery("title",key2))
                    .should(QueryBuilders.termQuery("content",key2)))
                    .withPageable(PageRequest.of(0,5))
                    .build();
            Page<SearchArticle> articlePage = searchArticleRepository.search(searchQuery);
            List<LikeArticleDTO> likeArticleDTOS = new ArrayList<>();
            for (SearchArticle searchArticle : articlePage.toList()) {
                User user = userService.findById(searchArticle.getUser_id());
                LikeArticleDTO likeArticleDTO = new LikeArticleDTO(user.getId(),user.getHeadUrl(),user.getNickname(),
                        searchArticle.getCreated_date(),searchArticle.getId(),
                        searchArticle.getTitle(), removeHtml(searchArticle.getContent()),searchArticle.getClicks(),
                        articleRepository.countCommentsByArticleId(searchArticle.getId()),
                        searchArticle.getLikes());
                likeArticleDTOS.add(likeArticleDTO);
            }
            return ResultVoUtil.success(likeArticleDTOS);
        }
        return ResultVoUtil.error(0, "文章不存在");
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
            article1.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            articleRepository.save(article1);
            List<Long> followersId = articleRepository.findFollowersByUserId(article1.getUserId());
            List<Timeline> timelines = new ArrayList<>();
            for (Long toUserId : followersId) {
                Timeline timeline = new Timeline();
                timeline.setArticleId(article1.getId());
                timeline.setToUserId(toUserId);
                timelines.add(timeline);
            }
            timelineRepository.saveAll(timelines);
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
            articleOptional.get().setClicks(articleOptional.get().getClicks()+1);
            articleRepository.save(articleOptional.get());
            boolean isLiked = false, isCollected = false, isFollowed = false;
            Article article = articleOptional.get();
            User user = userService.findById(article.getUserId());
            if (userId != null ) {
                isFollowed = followersService.countByFromUserIdAndToUserIdAndStatus(
                        userId, article.getUserId(), true
                ) > 0;
                isLiked = likeRepository.existsByTypeIdAndUserIdAndStatusAndType(article.getId(), userId, true, Type.ARTICLE);
                isCollected = collectionRepository.existsByArticleIdAndUserId(article.getId(),userId);
                ViewLogs viewLogs = viewLogsRepository.findByUserIdAndArticleId(userId, id);
                if (viewLogs == null) {
                    ViewLogs v = new ViewLogs();
                    v.setArticleId(id);
                    v.setUserId(userId);
                    v.setPreferDegree(0);
                    viewLogsRepository.save(v);
                }
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
            MessageLike messageLike = new MessageLike();
            Article article = articleRepository.getOne(l.getTypeId());
            messageLike.setTypeId(l.getTypeId());
            messageLike.setFromUserId(l.getUserId());
            messageLike.setToUserId(article.getUserId());
            messageLike.setMessageType(MessageType.ARTICLE);
            messageService.sendMessage(messageLike);
            ViewLogs viewLogs = viewLogsRepository.findByUserIdAndArticleId(l.getUserId(), l.getTypeId());
            if (viewLogs != null && viewLogs.getPreferDegree() < 1) {
                viewLogs.setPreferDegree(1);
                viewLogsRepository.save(viewLogs);
            }
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
    public ResultVo likeArticles(Long userId, int page) {
        Page<Likes> likesPage = likeRepository.findAllByUserIdAndStatusAndType(userId,true,
                PageRequest.of(page-1,4,Sort.by(Sort.Direction.DESC, "id")), Type.ARTICLE);
        List<Long> ids = new ArrayList<>();
        for (Likes likes : likesPage.toList()) {
            ids.add(likes.getTypeId());
        }
        List<Article> articles = articleRepository.findAllByIdIn(ids);
        List<LikeArticleDTO> likeArticleDTOS = new ArrayList<>();
        for (Article article : articles) {
            User user = userService.findById(article.getUserId());
            LikeArticleDTO articleDTO = new LikeArticleDTO(
                    user.getId(),user.getHeadUrl(),user.getNickname(),
                    article.getCreatedDate(), article.getId(),article.getTitle(), removeHtml(article.getContent()),
                    article.getClicks(),articleRepository.countCommentsByArticleId(article.getId()), article.getLikes()
            );
            likeArticleDTOS.add(articleDTO);
        }
        PageDTO<LikeArticleDTO> likeArticleDTOPageDTO = new PageDTO<>(likeArticleDTOS, likesPage.getTotalElements(),
                likesPage.getTotalPages());
        return ResultVoUtil.success(likeArticleDTOPageDTO);
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
        ViewLogs viewLogs = viewLogsRepository.findByUserIdAndArticleId(collection.getUserId(), collection.getArticleId());
        if (viewLogs != null && viewLogs.getPreferDegree() < 3) {
            viewLogs.setPreferDegree(3);
            viewLogsRepository.save(viewLogs);
        }
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

    @Override
    public ResultVo findHotArticles(int page) {
        Page<Article> articlePage = articleRepository.findAllByIsPublishedAndIsDeleted(true, false,
                PageRequest.of(page-1,5,Sort.by(Sort.Direction.DESC,"likes","clicks")));
        List<Article> articleList = articlePage.toList();
        List<SimpleArticleDTO> articleDTOS = new ArrayList<>();
        for (Article article : articleList) {
            User user = userService.findById(article.getUserId());
            SimpleArticleDTO simpleArticleDTO = new SimpleArticleDTO(
                   article.getId(), article.getTitle(), removeHtml(article.getContent()),
                    user.getId(), user.getNickname(),articleRepository.countCommentsByArticleId(article.getId()),article.getLikes()
            );
            articleDTOS.add(simpleArticleDTO);
        }
        PageDTO<SimpleArticleDTO> pageDTO = new PageDTO<>(articleDTOS,
                articlePage.getTotalElements(), articlePage.getTotalPages());
        return ResultVoUtil.success(pageDTO);
    }

    @Override
    public ResultVo findNewArticles() {
        Page<Article> articlePage = articleRepository.findAllByIsPublishedAndIsDeleted(true, false,
                PageRequest.of(0,5,Sort.by(Sort.Direction.DESC,"createdDate")));
        List<AsideArticleDTO> articleDTOS = new ArrayList<>();
        for (Article article :  articlePage.toList()) {
            AsideArticleDTO simpleArticleDTO = new AsideArticleDTO(
                    article.getId(), article.getTitle(), article.getClicks()
            );
            articleDTOS.add(simpleArticleDTO);
        }
        return ResultVoUtil.success(articleDTOS);
    }

    @Override
    public ResultVo getArticleByName(String name, Long topicId) {
        List<Article> articleList = articleRepository.findAllByTitleLikeAndIsPublishedAndIsDeletedAndUserId(
                "%"+name+"%", true, false,userService.getUserId());
        List<SimpleArticle2DTO> articles = new ArrayList<>();
        for (Article article : articleList) {
            boolean isCollected = topicArticleRepository
                    .existsByArticleIdAndIsPassedAndTopicId(article.getId(),true,topicId);
            SimpleArticle2DTO simpleArticle2DTO = new SimpleArticle2DTO(article.getId(), article.getTitle(),
                    isCollected);
            articles.add(simpleArticle2DTO);
        }
        return ResultVoUtil.success(articles);
    }

    @Override
    public ResultVo getUserArticlesByUserId(Long userId, int page, String order) {
        Page<Article> articlePage = articleRepository.
                findAllByUserIdAndIsPublishedAndIsDeleted(userId, true, false,
                        PageRequest.of(page-1,4,Sort.by(Sort.Direction.DESC,"isTop",order)));
        List<SimpleArticleNoteBookDTO> articleDTOS = new ArrayList<>();
        for (Article article : articlePage.toList()) {
            SimpleArticleNoteBookDTO simpleArticleDTO = new SimpleArticleNoteBookDTO(
                    article.getId(), article.getTitle(), removeHtml(article.getContent()),
                    article.getClicks(), articleRepository.countCommentsByArticleId(article.getId()),article.getLikes(),
                    article.getCreatedDate(), article.getIsTop()
            );
            articleDTOS.add(simpleArticleDTO);
        }
        PageDTO<SimpleArticleNoteBookDTO> pageDTO = new PageDTO<>(articleDTOS,
                articlePage.getTotalElements(), articlePage.getTotalPages());
        return ResultVoUtil.success(pageDTO);
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
