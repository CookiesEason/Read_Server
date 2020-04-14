package com.xzy.read.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/14 11:56
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Recommendations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long articleId;

    @CreatedDate
    private Timestamp date;

    private Boolean feedBack;

}
