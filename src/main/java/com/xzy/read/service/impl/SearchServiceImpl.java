package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.*;
import com.xzy.read.entity.*;
import com.xzy.read.entity.enums.FollowType;
import com.xzy.read.repository.*;
import com.xzy.read.service.SearchService;
import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author XieZhongYi
 * 2020/04/08 14:14
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    private static final String USER = "user";
    private static final String NOTEBOOK = "notebook";
    private static final String TOPIC = "topic";
    private static final String ARTICLE = "article";

    private UserRepository userRepository;

    private TopicRepository topicRepository;

    private NoteBooksRepository noteBooksRepository;

    private ArticleRepository articleRepository;

    private FollowersRepository followersRepository;

    private TopicArticleRepository topicArticleRepository;

    private FollowsRepository followsRepository;

    private SearchArticleRepository searchArticleRepository;

    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public SearchServiceImpl(UserRepository userRepository, TopicRepository topicRepository, NoteBooksRepository noteBooksRepository, ArticleRepository articleRepository, FollowersRepository followersRepository, TopicArticleRepository topicArticleRepository, FollowsRepository followsRepository, SearchArticleRepository searchArticleRepository, ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.noteBooksRepository = noteBooksRepository;
        this.articleRepository = articleRepository;
        this.followersRepository = followersRepository;
        this.topicArticleRepository = topicArticleRepository;
        this.followsRepository = followsRepository;
        this.searchArticleRepository = searchArticleRepository;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }


    @Override
    public ResultVo search(String content, int page, String type, String order) {
        if (USER.equals(type)) {
            return searchUsers(content, page);
        } else if (NOTEBOOK.equals(type)) {
            return searchNotebooks(content, page);
        } else if (TOPIC.equals(type)) {
            return searchTopics(content, page);
        } else if (ARTICLE.equals(type)) {
            return searchArticle(content, page, order);
        }
        return ResultVoUtil.success();
    }

    private ResultVo searchUsers(String content, int page) {
        Page<User> userPage = userRepository.findAllByNicknameLike("%" + content + "%",
                PageRequest.of(page-1,10));
        List<UserInfoDTO> userInfoDTOS = new ArrayList<>();
        Long loginUserId = userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName());
        for (User user : userPage.toList()) {
            Long userId = user.getId();
            Long followers = followersRepository.countAllByFromUserIdAndStatus(userId, true);
            Long fans = followersRepository.countAllByToUserIdAndStatus(userId, true);
            Long articles = articleRepository.countAllByUserIdAndIsDeleted(userId, false);
            Long words = articleRepository.countWordsByUserId(userId);
            Long likes = articleRepository.countLikesByUserId(userId);
            boolean isFollowed = followersRepository.countByFromUserIdAndToUserIdAndStatus(
                    loginUserId, userId, true) > 0;
            UserInfoDTO userInfoDTO = new UserInfoDTO(userId, user.getHeadUrl(), user.getNickname(),
                    user.getIntroduce(),
                    followers,fans,articles,words, likes ,isFollowed);
            userInfoDTOS.add(userInfoDTO);
        }
        PageDTO<UserInfoDTO> dto = new PageDTO<>(userInfoDTOS, userPage.getTotalElements(), userPage.getTotalPages());
        return ResultVoUtil.success(dto);
    }


    private ResultVo searchTopics(String content, int page) {
        Page<Topic> topicPage = topicRepository.findAllByNameLikeOrIntroduceLike("%" + content + "%",
                "%" + content + "%", PageRequest.of(page-1,10));
        Long userId = userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName());
        List<SearchTopicDTO> searchTopicDTOS = new ArrayList<>();
        for (Topic topic : topicPage.toList()) {
            boolean isFollowed = false;
            if (userId != null) {
                isFollowed =  followsRepository.existsByTypeIdAndFollowTypeAndStatusAndUserId(topic.getId(),FollowType.TOPIC,true, userId);
            }
            SearchTopicDTO searchTopicDTO = new SearchTopicDTO(
                    topic.getId(),topic.getHeadUrl(),topic.getName(),
                    topicArticleRepository.countByTopicIdAndIsPassed(topic.getId(),true),
                    followsRepository.countByTypeIdAndFollowTypeAndStatus(topic.getId(), FollowType.TOPIC, true),
                    isFollowed
            );
            searchTopicDTOS.add(searchTopicDTO);
        }
        PageDTO<SearchTopicDTO> dto = new PageDTO<>(searchTopicDTOS, topicPage.getTotalElements(), topicPage.getTotalPages());
        return ResultVoUtil.success(dto);
    }

    private ResultVo searchNotebooks(String content, int page) {
        Page<NoteBooks> noteBooksPage = noteBooksRepository.findAllByNameLike("%" + content + "%",
                PageRequest.of(page-1, 10));
        List<SearchNotebookDTO> searchNotebookDTOS = new ArrayList<>();
        Long userId = userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName());
        for (NoteBooks noteBooks : noteBooksPage.toList()) {
            Long articles = articleRepository.countByNotebookId(noteBooks.getId());
            Long followers = followsRepository.countByTypeIdAndFollowTypeAndStatus(noteBooks.getId(), FollowType.NOTEBOOK, true);
            SearchNotebookDTO searchNotebookDTO = new SearchNotebookDTO(
                    noteBooks.getId(), noteBooks.getName(), articles, followers,
                    followsRepository.existsByTypeIdAndFollowTypeAndStatusAndUserId(noteBooks.getId(),FollowType.NOTEBOOK,
                            true, userId)
            );
            searchNotebookDTOS.add(searchNotebookDTO);
        }
        PageDTO<SearchNotebookDTO> dto = new PageDTO<>(searchNotebookDTOS, noteBooksPage.getTotalElements(), noteBooksPage.getTotalPages());
        return ResultVoUtil.success(dto);
    }

    private ResultVo searchArticle(String content, int page, String order) {
        String preTag = "<em class=\"search-result-highlight\">";
        String postTag = "</em>";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().
                withQuery(QueryBuilders.multiMatchQuery(content, "title","content")).
                withHighlightFields(new HighlightBuilder.Field("title").numOfFragments(0).preTags(preTag).postTags(postTag),
                        (new HighlightBuilder.Field("content").numOfFragments(0).preTags(preTag).postTags(postTag)))
                .withPageable(PageRequest.of(page-1, 3))
                .withSort(SortBuilders.fieldSort(order).order(SortOrder.DESC))
                .build();
        Page<SearchArticle> searchArticlePage = searchArticleRepository.search(searchQuery);
        Page<SearchArticle> articles = elasticsearchRestTemplate.queryForPage(searchQuery, SearchArticle.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<SearchArticle> chunk = new ArrayList<>();
                for (SearchHit searchHit : searchResponse.getHits()) {
                    if (searchResponse.getHits().getHits().length <= 0) {
                        return null;
                    }
                    SearchArticle article = new SearchArticle();
                    HighlightField itemTitle = searchHit.getHighlightFields().get("title");
                    HighlightField itemContent = searchHit.getHighlightFields().get("content");
                    if (itemTitle != null) {
                        article.setTitle(itemTitle.fragments()[0].toString());
                    } else {
                        article.setTitle(String.valueOf(searchHit.getSourceAsMap().get("title")));
                    }
                    if (itemContent != null) {
                        article.setContent(removeHtml(itemContent.fragments()[0].toString()));
                    } else {
                        article.setContent(removeHtml(String.valueOf(searchHit.getSourceAsMap().get("content"))));
                    }
                    article.setId(Long.valueOf(searchHit.getSourceAsMap().get("id").toString()));
                    article.setClicks(Long.valueOf(String.valueOf(searchHit.getSourceAsMap().get("clicks"))));
                    article.setLikes(Long.valueOf(String.valueOf(searchHit.getSourceAsMap().get("likes"))));
                    article.setUser_id(Long.valueOf(String.valueOf(searchHit.getSourceAsMap().get("user_id"))));
                    article.setCreated_date(new Timestamp(formateDate(
                            String.valueOf(searchHit.getSourceAsMap().get("created_date"))).getTime()));
                    chunk.add(article);
                }
                if (chunk.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) chunk);
                }
                return null;
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });
        List<LikeArticleDTO> likeArticleDTOS = new ArrayList<>();
        if (articles!=null) {
            for (SearchArticle searchArticle : articles.toList()) {
                User user = userRepository.getOne(searchArticle.getUser_id());
                LikeArticleDTO likeArticleDTO = new LikeArticleDTO(user.getId(), user.getHeadUrl(), user.getNickname(),
                        searchArticle.getCreated_date(), searchArticle.getId(), searchArticle.getTitle(),searchArticle.getContent(),
                        searchArticle.getClicks(),
                        articleRepository.countCommentsByArticleId(searchArticle.getId()),searchArticle.getLikes());
                likeArticleDTOS.add(likeArticleDTO);
            }
        }
        PageDTO<LikeArticleDTO> searchArticlePageDTO = new PageDTO<>(likeArticleDTOS,
                searchArticlePage.getTotalElements(), searchArticlePage.getTotalPages());
        return ResultVoUtil.success(searchArticlePageDTO);
    }

    private String removeHtml (String content) {
        if (content == null) {
            return "";
        }
        return Jsoup.clean(content, new Whitelist().addTags("em").addAttributes("em","class"));
    }

    private static Date formateDate(String dateStr){
        try {
            dateStr = dateStr.replace("Z", " UTC");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            return format.parse(dateStr);
        } catch (Exception e) {
            log.error("转换时间失败！", e);
        }
        return null;
    }

}
