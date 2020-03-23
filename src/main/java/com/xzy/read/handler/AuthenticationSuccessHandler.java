package com.xzy.read.handler;

import com.alibaba.fastjson.JSON;
import com.xzy.read.dto.UserTokenDTO;
import com.xzy.read.entity.User;
import com.xzy.read.service.UserService;
import com.xzy.read.util.JwtUtil;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author XieZhongYi
 * 2020/03/23 17:01
 */
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        User user = service.findByTelephone(authentication.getName());
        UserTokenDTO userTokenDTO = new UserTokenDTO(
                user.getNickname(),user.getTelephone(),user.getHeadUrl(),user.getSex(),
                user.getIntroduce(),user.getRole(), JwtUtil.generateToken(user)
        );
        response.getWriter().print(JSON.toJSON(ResultVoUtil.success(userTokenDTO)));
    }

}
