package com.xzy.read.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/13 20:22
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class ViewLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long articleId;

    @CreatedDate
    private Timestamp viewTime;

    /**
     * 用户对文章的偏好程度(prefer_degree[0:仅仅浏览，1:喜欢，2:评论，3:收藏])。
     */
    private int preferDegree;

}
