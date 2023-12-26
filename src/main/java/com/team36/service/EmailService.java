package com.team36.service;

public interface EmailService {
    String sendSimpleMessage(String to)throws Exception;
    void sendTempPasswordEmail(String email, String tempPassword);
    String getRamdomPassword(int size);
}
