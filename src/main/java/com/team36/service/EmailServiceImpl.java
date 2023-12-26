package com.team36.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
}