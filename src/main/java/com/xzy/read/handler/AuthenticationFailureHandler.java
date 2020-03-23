package com.xzy.read.handler;

import com.alibaba.fastjson.JSON;
import com.xzy.read.util.ResultVoUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author XieZhongYi
 * 2020/03/23 16:56
 */
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json; charset=utf-8");
        String res = JSON.toJSONString(ResultVoUtil.error(0,"账号或密码错误"));
        if (exception.getCause()!=null){
            res = JSON.toJSONString(ResultVoUtil.error(0,exception.getMessage()));
        }
        response.getWriter().print(res);
    }
}
