package com.xzy.read.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/03/25 14:47
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private Long words = 0L;

    private Boolean isTop = false;

    private Boolean isPublished = false;

    private Boolean isDeleted = false;

    @CreatedDate
    private Timestamp createdDate;

    private Timestamp recentCommentDate;

    private Long notebookId;

    private Long userId;

}
