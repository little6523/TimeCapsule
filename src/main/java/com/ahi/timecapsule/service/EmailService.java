package com.ahi.timecapsule.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Value("${spring.mail.username}")
  private String fromEmail;

  private final JavaMailSender javaMailSender;

  public EmailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  // 임시 비밀번호 발급 이메일 발송
  public void sendTempPasswordEmail(String toEmail, String tempPassword) {
    String subject = "[TimeCapsule] 임시 비밀번호 안내";
    String content =
        "안녕하세요.\n\n"
            + "요청하신 임시 비밀번호를 안내드립니다.\n\n"
            + "임시 비밀번호: "
            + tempPassword
            + "\n\n"
            + "보안을 위해 로그인 후 즉시 비밀번호를 변경해주시기 바랍니다.\n"
            + "만약 이 요청을 본인이 하지 않으셨다면, 이 이메일을 무시하시고 "
            + "계정 보안을 위해 비밀번호를 즉시 변경해주시기 바랍니다.\n\n"
            + "감사합니다.\n"
            + "[TimeCapsule] 팀 드림";
    sendEmail(toEmail, subject, content);
  }

  // 공통 메서드(이메일)
  public void sendEmail(String to, String subject, String content) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromEmail); // 보내는 사람 이메일 아이디
    message.setTo(to); // 받는 사람 이메일 아이디
    message.setSubject(subject); // 이메일 제목
    message.setText(content); // 이메일 내용
    javaMailSender.send(message); // 이메일 전송
  }
}
