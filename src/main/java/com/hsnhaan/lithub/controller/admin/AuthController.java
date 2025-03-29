package com.hsnhaan.lithub.controller.admin;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.Admin;
import com.hsnhaan.lithub.security.AdminDetails;
import com.hsnhaan.lithub.service.Implement.AdminServiceImpl;

@Controller
public class AuthController {

	private final AdminServiceImpl adminSvc;
	
	public AuthController(AdminServiceImpl adminSvc) {
		this.adminSvc = adminSvc;
	}
	
	@GetMapping("/admin/login")
	public String loginForm()  {
		return  "admin/login";
	}
	
	@GetMapping("/admin/change-password")
	public String changePasswordForm() {
		return "admin/change-password";
	}
	
	@PatchMapping("/admin/change-password")
	public String changePassword(RedirectAttributes redirectAttributes, Authentication authentication,
			@RequestParam String password) {
		Admin admin = ((AdminDetails) authentication.getPrincipal()).getAdmin();
		adminSvc.changePassword(admin.getUsername(), password);
		redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
		if (admin.getRole().equals("SUPER_ADMIN"))
			return "redirect:/admin/account";
		return "redirect:/admin/story";
	}
	
}
