package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/04/06 14:08
 */
@Data
@AllArgsConstructor
public class SimpleUserDTO {

    private Long userId;

    private String headUrl;

    private String nickname;

}
