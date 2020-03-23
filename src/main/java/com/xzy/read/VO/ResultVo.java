package com.xzy.read.VO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author XieZhongYi
 * 2020/03/23 16:27
 */
@Data
public class ResultVo<T> implements Serializable {

    private static final long serialVersionUID = 430185373222528745L;

    /**错误码 */
    private Integer code;

    /**提示信息*/
    private String msg;

    /**具体内容*/
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

}
