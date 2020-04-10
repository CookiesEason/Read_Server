package com.xzy.read.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author XieZhongYi
 * 2020/03/25 14:47
 */
@Document(indexName = "article",type = "docs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchArticle {


    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    private Long words;

    private Long clicks;

    private Long likes;

    private Boolean is_top;

    private Boolean is_published;

    private Boolean is_deleted;

    private Timestamp created_date;

    private Timestamp recent_commentDate;

    private Long notebook_id;

    private Long user_id;

}
