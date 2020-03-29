package com.xzy.read.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/03/29 19:36
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long articleId;

    private Long commentId;

    private Long toUserId;

    private Long fromUserId;

    private String content;

    @CreatedDate
    private Timestamp createdDate;

}
