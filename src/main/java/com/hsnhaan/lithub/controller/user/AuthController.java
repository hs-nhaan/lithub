package com.hsnhaan.lithub.controller.user;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.model.User;
import com.hsnhaan.lithub.security.CustomUserDetails;
import com.hsnhaan.lithub.service.Implement.GenreServiceImpl;
import com.hsnhaan.lithub.service.Implement.UserServiceImpl;

@Controller("UserAuthController")
public class AuthController {

	private final GenreServiceImpl genreSvc;
	private final UserServiceImpl userSvc;
	
	public AuthController(GenreServiceImpl genreSvc, UserServiceImpl userSvc) {
		this.genreSvc = genreSvc;
		this.userSvc = userSvc;
	}
	
	@GetMapping("/dang-nhap")
	public String login() {
		return "user/login";
	}
	
	@GetMapping("/dang-ky")
	public String register() {
		return "user/register";
	}
	
	@PostMapping("/dang-ky")
	@ResponseBody
	public String doRegister(@RequestParam String email, @RequestParam String password, 
			@RequestParam String rePassword, @RequestParam String username) {
		userSvc.save(email, username, password, rePassword);		
		return "Vui lòng nhấn vào link trong email chúng tôi đã gửi cho bạn để xác nhận";
	}
	
	@GetMapping("/verify")
	@ResponseBody
	public String verify(@RequestParam String token) {
		userSvc.verify(token);
		return "Xác thực thành công, bây giờ bạn đã có thể đăng nhập";
	}
	
	@GetMapping("/tai-khoan")
	public String account(Model model, Authentication authentication) {
		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
		List<Genre> genres = genreSvc.getAll();
		model.addAllAttributes(Map.of(
			"user", user,
			"genres", genres
		));
		return "user/account";
	}
	
	@GetMapping("/tai-khoan/doi-mat-khau")
	public String changePassword() {
		return "user/change-password";
	}
	
	@PatchMapping("/tai-khoan/doi-mat-khau")
	public String doChangePassword(RedirectAttributes redirectAttributes,
			@RequestParam String password, Authentication authentication) {
		User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
		userSvc.changePassword(user.getEmail(), password);
		redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
		return "redirect:/tai-khoan";
	}
	
}