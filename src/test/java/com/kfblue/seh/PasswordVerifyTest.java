package com.kfblue.seh;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordVerifyTest {

    public static void main(String[] args) {
        String pwd = BCrypt.hashpw("admin123", BCrypt.gensalt());
        System.out.println(pwd);
        System.out.println(BCrypt.checkpw("admin123", pwd));
    }
}