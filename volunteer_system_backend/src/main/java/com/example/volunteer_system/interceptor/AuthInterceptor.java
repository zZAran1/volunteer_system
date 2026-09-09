package com.example.volunteer_system.interceptor;

import com.example.volunteer_system.exception.TokenException;
import com.example.volunteer_system.model.entity.Users;
import com.example.volunteer_system.service.UserService;
import com.example.volunteer_system.util.JwtUtil;
import com.example.volunteer_system.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        String token = request.getHeader("Authorization");
        if(token==null||token.isEmpty()) {
            throw new TokenException("还未登录");
        }
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if(!jwtUtil.validateToken(token)) {
            throw new TokenException("登录失效,请重新登录");
        }
        int user_id= Integer.parseInt(jwtUtil.getUserId(token));
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey("user:ban:"+user_id))) {
            throw new TokenException("该账号已被封禁，请联系管理员");
        }
        Users users =userService.lambdaQuery()
                .eq(Users::getId, user_id)
                .select(Users::getStatus)
                .one();
        if(users==null){
            throw new TokenException("登录失效，请重新登录");
        }
        if(users.getStatus()!=1){
            throw new TokenException("该账号已被封禁，请联系联系管理员");
        }
        if(!Boolean.TRUE.equals(stringRedisTemplate.hasKey("session:"+user_id))) {
            throw new TokenException("登录已失效，请重新登录");
        }
        UserContext.setUserId(user_id);
        int role= Integer.parseInt(jwtUtil.getRole(token));
        UserContext.setRole(role);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }
}
