package com.team36;

import com.team36.domain.Profile;
import com.team36.dto.MemberJoinDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Scanner;

@SpringBootTest
@Log4j2
public class ProfileTest {

    public static void main(String[] args) {
        String a = "/dir2";

        String b = a.split("/")[1].toString();
        System.out.println(b);
        System.out.println(a.split("/").toString());
    }

}
