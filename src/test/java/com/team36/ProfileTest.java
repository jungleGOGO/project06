package com.team36;

import com.team36.domain.Profile;
import com.team36.dto.MemberJoinDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

@SpringBootTest
@Log4j2
public class ProfileTest {
    public static void main(String[] args) {
        char[] charSet = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '!', '@', '#', '$', '%', '^', '&' };

        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());
        int idx = 0;
        int len = charSet.length;
        for (int i=0; i<7; i++) {
            idx = (int) (len * Math.random());
            idx = sr.nextInt(len);
            // 강력한 난수를 발생시키기 위해 SecureRandom을 사용한다.
            sb.append(charSet[idx]);
        }
        System.out.println(sb.toString());
    }

}
