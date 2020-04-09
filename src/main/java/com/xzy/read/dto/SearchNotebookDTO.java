package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/08 19:42
 */
@Data
@AllArgsConstructor
public class SearchNotebookDTO {

    private Long id;

    private String name;

    private Long articles;

    private Long followers;

    private Boolean isFollowed;

}
