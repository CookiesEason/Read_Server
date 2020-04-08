package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/07 11:26
 */
@Data
@AllArgsConstructor
public class MessageDTO {

    private Long comments;

    private Long likes;

    private Long submits;

    private Long follows;

    private Long others;

}
