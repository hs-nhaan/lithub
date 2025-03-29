package com.hsnhaan.lithub.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AdminAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		HttpSession session = request.getSession();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		session.setAttribute("ADMIN_SESSION", userDetails);
		String role = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("ROLE_USER");
		switch (role) {
		case "ROLE_SUPER_ADMIN":
			response.sendRedirect("/admin/account");
			break;
		case "ROLE_ADMIN":
			response.sendRedirect("/admin/story");
			break;
		default:
			response.sendRedirect("/home");
		}
	}

}
