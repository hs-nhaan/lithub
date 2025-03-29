package com.hsnhaan.lithub.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hsnhaan.lithub.model.Admin;
import com.hsnhaan.lithub.service.Implement.AdminServiceImpl;

@Service
public class AdminDetailsService implements UserDetailsService {

	private final AdminServiceImpl adminSvc;
	
	public AdminDetailsService(AdminServiceImpl adminSvc) {
		this.adminSvc = adminSvc;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Admin admin = adminSvc.getByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return new AdminDetails(admin);
	}

}
