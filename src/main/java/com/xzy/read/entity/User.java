package com.xzy.read.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * @author XieZhongYi
 * 用户表
 * 2020/03/23 15:14
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String telephone;

    @JsonIgnore
    private String password;

    private String headUrl;

    private String sex;

    private String introduce;

    private String role;


}
