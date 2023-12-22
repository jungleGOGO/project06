package com.team36;

import com.team36.domain.Profile;
import com.team36.dto.MemberJoinDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Scanner;

@SpringBootTest
@Log4j2
public class ProfileTest {

    public static void main(String[] args) {
        String a = "\\2\\._2New";
//        String a = "/dir";
        String[] b =a.split("[\\\\/]");
        String result = b[b.length-1];

        String c =a.replace(".java","");
        System.out.println("1 : "+ Arrays.toString(b));
        System.out.println("2 : "+result);
        System.out.println("3: "+ c);
    }

}
