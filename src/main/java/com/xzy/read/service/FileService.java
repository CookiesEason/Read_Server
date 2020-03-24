package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author XieZhongYi
 * 2020/03/24 20:56
 */
public interface FileService {

    ResultVo uploadFile(MultipartFile multipartFile);

}
