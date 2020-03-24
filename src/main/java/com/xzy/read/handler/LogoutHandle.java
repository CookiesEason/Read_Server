package com.xzy.read.handler;

import com.alibaba.fastjson.JSON;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author XieZhongYi
 * 2020/03/24 14:03
 */
public class LogoutHandle extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        String res = JSON.toJSONString(ResultVoUtil.success());
        response.getWriter().write(res);
    }

}
