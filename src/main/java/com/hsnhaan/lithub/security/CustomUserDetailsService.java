package com.hsnhaan.lithub.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hsnhaan.lithub.model.User;
import com.hsnhaan.lithub.service.Implement.UserServiceImpl;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserServiceImpl userSvc;
	
	public CustomUserDetailsService(UserServiceImpl userSvc) {
		this.userSvc = userSvc;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println("Email can dang nhap: " + email);
		User user = userSvc.getByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		return new CustomUserDetails(user);
	}

}
