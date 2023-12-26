package com.team36.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    JavaMailSender emailSender;
    private String ePw;

    private String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }

    private MimeMessage createMessage(String to, String ePw) throws Exception {
        System.out.println("보내는 대상: " + to);
        System.out.println("인증 번호: " + ePw);
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("Tcoding 이메일 인증코드");

        String msgg = "";
        msgg += "<div style='background-color: #f4f4f4; padding: 20px; text-align: center;'>";
        msgg += "<h1 style='color: #3498db; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'>안녕하세요, Tcoding입니다.</h1>";
        msgg += "<p style='font-size: 16px; color: #555;'>아래 코드를 복사하여 입력해주세요</p>";
        msgg += "<div style='background-color: #3498db; color: white; padding: 15px; border-radius: 5px; display: inline-block;'>";
        msgg += "<h3 style='margin: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;'>회원가입 인증 코드</h3>";
        msgg += "<p style='font-size: 20px; margin: 10px 0 0;'>" + ePw + "</p>";
        msgg += "</div>";
        msgg += "<p style='font-size: 16px; color: #555; margin-top: 20px;'>감사합니다.</p>";
        msgg += "</div>";

        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("beubeuvv@gmail.com", "project06"));

        return message;
    }

    @Override
    public String sendSimpleMessage(String to) throws Exception {
        // 새 키 생성
        String ePw = createKey();

        MimeMessage message = createMessage(to, ePw);

        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        // 새 키를 반환
        return ePw;
    }

    public String getVerificationCode() {
        return ePw;
    }

    @Override
    public void sendTempPasswordEmail(String email, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("비밀번호 재설정");
        message.setText("귀하의 새로운 임시 비밀번호는 " + tempPassword + " 입니다. 로그인 후 비밀번호를 변경해 주세요.");
        message.setFrom("juncheol08@naver.com");
        emailSender.send(message);

    }

    @Override
    public String getRamdomPassword(int size) {
        char[] charSet = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '!', '@', '#', '$', '%', '^', '&' };
        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());
        int idx = 0;
        int len = charSet.length;
        for (int i=0; i<size; i++) {
            idx = (int) (len * Math.random());
            idx = sr.nextInt(len);
            // 강력한 난수를 발생시키기 위해 SecureRandom을 사용한다.
            sb.append(charSet[idx]);
        }
        return sb.toString();    }
}