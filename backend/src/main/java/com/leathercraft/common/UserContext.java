package com.leathercraft.common;

public class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static Long getCurrentUserId() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    public static Long requireCurrentUserId() {
        Long userId = CURRENT_USER.get();
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }
}
