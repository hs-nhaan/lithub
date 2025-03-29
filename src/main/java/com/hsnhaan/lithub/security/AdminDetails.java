package com.hsnhaan.lithub.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hsnhaan.lithub.model.Admin;

public class AdminDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final Admin admin;
	
	public AdminDetails(Admin admin) {
		this.admin = admin;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole()));
	}
	
	public Admin getAdmin() {
		return admin;
	}
	
	public String getFull_name() {
		return admin.getFull_name();
	}
	
	public String getRole( ) {
		return admin.getRole();
	}

	@Override
	public String getPassword() {
		return admin.getPassword();
	}

	@Override
	public String getUsername() {
		return  admin.getUsername();
	}

}
