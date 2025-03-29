package com.hsnhaan.lithub.service;

public interface IEmailService {

	void sendVerificationEmail(String to, String token);
	
}
