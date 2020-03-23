package com.xzy.read.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author XieZhongYi
 * 2020/03/23 20:06
 */
@Data
@AllArgsConstructor
public class UserTokenDTO {

    private String nickname;

    private String telephone;

    private String headUrl;

    private String sex;

    private String introduce;

    private String role;

    private String token;

}
