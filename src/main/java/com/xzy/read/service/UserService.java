package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author XieZhongYi
 * 2020/03/23 15:54
 */
public interface UserService {

    User findByTelephone(String telephone);

    Long getUserId();

    User findById(Long userId);

    ResultVo save(User user);

    ResultVo getUserInfo();

    ResultVo update(User user);

    ResultVo uploadHead(MultipartFile multipartFile);

    ResultVo resetPassword(User user);

    ResultVo resetTelephone(User user);

}
