package com.dev.sphere.userService.auth;

public class UserContextHolder {

    private static ThreadLocal<Long> currentUser = new ThreadLocal<>();

    public static Long getCurrentUser() {
        return currentUser.get();
    }
    static void setCurrentUser(Long userId) {
        currentUser.set(userId);
    }

    static void removeCurrentUser() {
        currentUser.remove();
    }
}
