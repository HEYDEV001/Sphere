package com.dev.sphere.userService.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String password) {// This will hash the password for the first time
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashedPassword) { // This will check the password with the hashed password and return boolean value.
        return BCrypt.checkpw(password, hashedPassword);
    }
}
