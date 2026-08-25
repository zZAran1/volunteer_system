package com.example.volunteer_system.util;


import com.example.volunteer_system.exception.TokenException;

public class UserContext {
    private  static final ThreadLocal<Integer> USER_ID=new ThreadLocal<>();
    private  static final ThreadLocal<Integer> RoLE=new ThreadLocal<>();

    public static void setUserId(int userId){
        USER_ID.set(userId);
    }
    public static void setRole(int role){
        RoLE.set(role);
    }
    public static int getUserId(){
        Integer userId = USER_ID.get();
        if(userId==null){
            throw new TokenException("用户未登录，请先登录");
        }
        return userId;
    }
    public static int getRole(){
        Integer role = RoLE.get();
        if(role==null){
            throw new TokenException("用户未登录，请先登录");
        }
        return role;
    }
    public static void remove(){
        USER_ID.remove();
        RoLE.remove();
    }
}
