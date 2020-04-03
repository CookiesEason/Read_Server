package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/03 11:00
 */
@Data
@AllArgsConstructor
public class SimpleNoteBookDTO {

    private Long id;

    private String name;

    private Long articles;

    private Long words;

    private Long followers;

    private Long userId;

    private String headUrl;

    private String nickname;

    private Boolean isFollowed;

}
