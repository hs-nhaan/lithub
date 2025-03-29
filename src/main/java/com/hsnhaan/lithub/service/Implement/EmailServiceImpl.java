package com.hsnhaan.lithub.service.Implement;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.hsnhaan.lithub.service.IEmailService;

@Service
public class EmailServiceImpl implements IEmailService {

	private final JavaMailSender mailSender;
	
	public EmailServiceImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	@Override
	public void sendVerificationEmail(String to, String token) {
		try {
			String sub = "Xác nhận tài khoản";
			String url = "http://localhost:8080/verify?token=" + token;
			String body = "Truy cập vào link sau để xác nhận: " + url;
			
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(sub);
			message.setText(body);
			mailSender.send(message);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
