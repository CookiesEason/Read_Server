package com.xzy.read.service.impl;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.sun.xml.bind.v2.TODO;
import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.LikeArticleDTO;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.Recommendations;
import com.xzy.read.entity.User;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.RecommendationsRepository;
import com.xzy.read.repository.UserRepository;
import com.xzy.read.service.RecommendService;
import com.xzy.read.util.ResultVoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author XieZhongYi
 * 2020/04/13 21:26
 */
@Service
@Slf4j
public class RecommendServiceImpl implements RecommendService {

    public static final int N = 8;

    private UserRepository userRepository;

    private RecommendationsRepository recommendationsRepository;

    private ArticleRepository articleRepository;

    public RecommendServiceImpl(UserRepository userRepository, RecommendationsRepository recommendationsRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.articleRepository = articleRepository;
    }


    @Override
    public ResultVo recommendByUserId(Long userId, int page) {
        Page<Recommendations> recommendations = recommendationsRepository.getAllByUserId(userId,
                PageRequest.of(page-1, 5));
        List<LikeArticleDTO> articleDTOS = new ArrayList<>();
        User user = userRepository.getOne(userId);
        for (Recommendations recommendation : recommendations.toList()) {
            Article article = articleRepository.getOne(recommendation.getArticleId());
            LikeArticleDTO articleDTO = new LikeArticleDTO(user.getId(), user.getHeadUrl(), user.getNickname(),
                    article.getCreatedDate(), article.getId(), article.getTitle(), removeHtml(article.getContent()),
                    article.getClicks(), articleRepository.countCommentsByArticleId(article.getId()),
                    article.getLikes());
            articleDTOS.add(articleDTO);
        }
        PageDTO<LikeArticleDTO> likeArticleDTOPageDTO = new PageDTO<>(articleDTOS,
                recommendations.getTotalElements(), recommendations.getTotalPages());
        return ResultVoUtil.success(likeArticleDTOPageDTO);
    }

    /**
     * todo 定时
     */
    private void recommend() {
        try {
            JDBCDataModel dataModel = init();
            List<User> users = userRepository.findAll();
            UserSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
            // NearestNeighborhood的数量有待考察
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, dataModel);
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            for (User user : users) {
                List<RecommendedItem> recItems = recommender.recommend(user.getId(), N);
                List<Recommendations> recommendationsList = new ArrayList<>();
                Set<Long> hs = new HashSet<>();
                for (RecommendedItem recItem : recItems) {
                    hs.add(recItem.getItemID());
                }
                filterReccedArticle(hs, user.getId());
                for (Long h : hs) {
                    Recommendations recommendations = new Recommendations();
                    recommendations.setArticleId(h);
                    recommendations.setUserId(user.getId());
                    recommendationsList.add(recommendations);
                }
                recommendationsRepository.saveAll(recommendationsList);
                log.info("user:" + user.getNickname() + "推荐列表" + recItems);
            }
        } catch (TasteException e) {
            log.error("CB算法构造偏好对象失败！");
            e.printStackTrace();
        }
        catch (Exception e) {
            log.error("CB算法数据库操作失败！");
            e.printStackTrace();
        }
    }


    private JDBCDataModel init() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName("localhost");
        dataSource.setUrl("jdbc:mysql://localhost:3306/read?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUser("root");
        dataSource.setPassword("123456789a");
        dataSource.setDatabaseName("read");
        JDBCDataModel jdbcDataModel = new MySQLJDBCDataModel(dataSource, "view_logs",
                "user_id", "article_Id",
                "prefer_degree", "view_time");
        return jdbcDataModel;
    }

    public void filterReccedArticle(Collection<Long> col, Long userId)
    {
        try
        {
            //但凡近期已经给用户推荐过的新闻，都过滤掉
            List<Recommendations> recommendationList = recommendationsRepository.findAllByUserId(userId);
            for (Recommendations recommendation:recommendationList)
            {
                if (col.contains(recommendation.getArticleId()))
                {
                    col.remove(recommendation.getArticleId());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String removeHtml (String content) {
        if (content == null) {
            return "";
        }
        Document doc = Jsoup.parse(content);
        return doc.text();
    }


    public static void main(String[] args) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName("localhost");
        dataSource.setUrl("jdbc:mysql://localhost:3306/read?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8");
        dataSource.setUser("root");
        dataSource.setPassword("123456789a");
        dataSource.setDatabaseName("read");
        JDBCDataModel jdbcDataModel = new MySQLJDBCDataModel(dataSource, "view_logs",
                "user_id", "article_Id",
                "prefer_degree", "view_time");
        UserSimilarity similarity = new LogLikelihoodSimilarity(jdbcDataModel);
        // NearestNeighborhood的数量有待考察
        UserNeighborhood neighborhood;
        try {
            neighborhood = new NearestNUserNeighborhood(2, similarity, jdbcDataModel);
            Recommender recommender = new GenericUserBasedRecommender(jdbcDataModel, neighborhood, similarity);
            List<RecommendedItem> recItems = recommender.recommend(1, 10);
            System.out.println(recItems);
        } catch (TasteException e) {
            e.printStackTrace();
        }
    }



}
