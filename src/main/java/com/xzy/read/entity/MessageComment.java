package com.xzy.read.entity;

import com.xzy.read.entity.enums.MessageType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/06 21:29
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class MessageComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long commentId;

    private String content;

    private Long articleId;

    private String title;

    private Long fromUserId;

    private Long toUserId;

    private Boolean isRead = false;

    @CreatedDate
    private Timestamp createdDate;

}
