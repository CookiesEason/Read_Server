package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/29 19:24
 */
@Data
@AllArgsConstructor
public class PageDTO<T> {

    private List<T> list;

    private Long totalElements;

    private Integer totalPages;

}
