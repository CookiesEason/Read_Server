package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;

/**
 * @author XieZhongYi
 * 2020/04/13 21:25
 */
public interface RecommendService {

    ResultVo recommendByUserId(Long userId, int page);

}
