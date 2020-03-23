package com.xzy.read.service;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.User;

/**
 * @author XieZhongYi
 * 2020/03/23 15:54
 */
public interface UserService {

    User findByTelephone(String telephone);

    ResultVo save(User user);

}
