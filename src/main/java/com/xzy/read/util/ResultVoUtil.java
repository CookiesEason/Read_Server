package com.xzy.read.util;

import com.xzy.read.VO.ResultVo;

/**
 * @author XieZhongYi
 * 2020/03/23 16:40
 */
public class ResultVoUtil {

    public static ResultVo success(){
        ResultVo<Object> resultVo = new ResultVo<>();
        resultVo.setCode(1);
        resultVo.setMsg("成功");
        return resultVo;
    }

    public static ResultVo success(Object object){
        ResultVo<Object> resultVo = new ResultVo<>();
        resultVo.setCode(1);
        resultVo.setMsg("成功");
        resultVo.setData(object);
        return resultVo;
    }

    public static ResultVo error(Integer code, String msg){
        ResultVo resultVo = new ResultVo();
        resultVo.setCode(code);
        resultVo.setMsg(msg);
        return resultVo;
    }

}
