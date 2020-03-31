package com.xzy.read.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/03/29 18:45
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String content;

    private Long articleId;

    private Long likes = 0L;

    @CreatedDate
    private Timestamp createdDate;

}
