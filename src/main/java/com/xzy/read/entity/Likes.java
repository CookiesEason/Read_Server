package com.xzy.read.entity;

import com.xzy.read.entity.enums.Type;
import lombok.Data;

import javax.persistence.*;

/**
 * @author XieZhongYi
 * 2020/03/29 21:12
 */
@Entity
@Data
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long typeId;

    private Boolean status;

    @Enumerated(EnumType.STRING)
    private Type type;

}
