package com.xzy.read.entity;

import com.xzy.read.entity.enums.MessageType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/07 21:14
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class MessageOther {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long articleId;

    private Long topicId;

    private String reason;

    private Boolean isRejected;

    private Long toUserId;

    private Boolean isRead = false;

    @CreatedDate
    private Timestamp createdDate;

}
