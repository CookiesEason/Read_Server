package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;

/**
 * @author XieZhongYi
 * 2020/04/08 14:12
 */
public interface SearchService {

    ResultVo search(String content, int page, String type);

}
