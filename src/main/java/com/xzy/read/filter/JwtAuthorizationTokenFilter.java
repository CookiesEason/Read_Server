package com.xzy.read.filter;

import com.xzy.read.entity.User;
import com.xzy.read.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/23 18:44
 */
@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = httpServletRequest.getHeader(JwtUtil.TOKEN_HEADER);
        if (token!=null && JwtUtil.parseToken(token.replace(JwtUtil.TOKEN_PREFIX,""))) {
            logger.info("Token验证通过");
            String key = token.replace(JwtUtil.TOKEN_PREFIX,"");
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(JwtUtil.getValue(key,"role"));
            UsernamePasswordAuthenticationToken user =
                    new UsernamePasswordAuthenticationToken(JwtUtil.getValue(key,"telephone")
                            , null, authorities);
            SecurityContextHolder.getContext().setAuthentication(user);
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
