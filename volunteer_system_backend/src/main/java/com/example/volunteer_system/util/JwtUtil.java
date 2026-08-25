package com.example.volunteer_system.util;
import com.example.volunteer_system.exception.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String base64Secret;
    @Value("${jwt.expiration}")
    private long expirationTime;
    private SecretKey secretKey;
    private SecretKey getSigningKey() {
        if (secretKey == null) {
            byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
            secretKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return secretKey;
    }
    //生成token
    public String generateToken(String userId,String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }
    //解析token
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    //校验
    public boolean validateToken(String token){
        try{
            parseToken(token);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    //获取userId
    public String getUserId(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (ExpiredJwtException e) {
            throw new TokenException("登录已过期");
        } catch (Exception e) {
            throw new TokenException("登录失效");
        }
    }
    public String getRole(String token) {
        try {
            return parseToken(token).get("role", String.class);
        } catch (ExpiredJwtException e) {
            throw new TokenException("登录已过期");
        } catch (Exception e) {
            throw new TokenException("登录失效");
        }
    }
}
