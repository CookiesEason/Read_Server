package com.xzy.read.entity;

import com.xzy.read.entity.enums.FollowType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/04/01 19:11
 */
@Entity
@Data
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Follows {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long typeId;

    private Long userId;

    private Boolean status;

    @Enumerated(EnumType.STRING)
    private FollowType followType;

    @CreatedDate
    private Timestamp createdDate;


}
