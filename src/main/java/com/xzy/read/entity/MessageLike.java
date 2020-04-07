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
@EntityListeners(AuditingEntityListener.class)
@Data
public class MessageLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long typeId;

    private Long fromUserId;

    private Long toUserId;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private Boolean isRead = false;

    @CreatedDate
    private Timestamp createdDate;

}
