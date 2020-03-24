package com.xzy.read.util;

import com.xzy.read.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XieZhongYi
 * 2020/03/23 20:09
 */
@Slf4j
public class JwtUtil {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer";

    public static String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("telephone",user.getTelephone());
        claims.put("role",user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, TOKEN_PREFIX)
                .setExpiration(generateExpirationDate())
                .compact();

    }
    public static boolean parseToken(String token){
        try {
            Jwts.parser().setSigningKey(TOKEN_PREFIX).parse(token);
            return true;
        }catch (Exception e){
            log.error("Token 验证错误");
        }
        return false;
    }
    public static String getValue(String token, String key){
        try {
            return (String) Jwts.parser().setSigningKey(TOKEN_PREFIX).parseClaimsJws(token).getBody().get(key);
        }catch (Exception e){
            return null;
        }
    }
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + 9600*1000);
    }

    public static Boolean isTokenExpired(String token) {
        final Date expiration = Jwts.parser().setSigningKey(TOKEN_PREFIX).parseClaimsJws(token.replace(TOKEN_PREFIX,"")).getBody().getExpiration();
        return expiration.before(new Date());
    }
}
