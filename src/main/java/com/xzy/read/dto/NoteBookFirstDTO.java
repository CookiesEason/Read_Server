package com.xzy.read.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/03/26 17:01
 */
@Data
@AllArgsConstructor
public class NoteBookFirstDTO {

    private Long id;

    private String name;

    private Long userId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long firstArticleId;

}
