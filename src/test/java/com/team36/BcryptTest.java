package com.team36;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {

    public static void main(String[] args) {
        String pw = "1234";
        BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();
        System.out.println(pwEncoder.encode(pw));
    }
}
