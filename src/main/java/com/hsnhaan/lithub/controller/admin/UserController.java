package com.hsnhaan.lithub.controller.admin;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.User;
import com.hsnhaan.lithub.service.Implement.UserServiceImpl;
import com.hsnhaan.lithub.util.Config;

import jakarta.validation.constraints.Min;

@Validated
@Controller
public class UserController {

	private final UserServiceImpl userSvc;
	
	public UserController(UserServiceImpl userSvc) {
		this.userSvc = userSvc;
	}
	
	@GetMapping("/admin/user")
	public String list(Model model, @RequestParam(defaultValue = "1") @Min(1) int page,
						@RequestParam(required = false) String keyword) {
		Page<User> users = null;
		
		if (StringUtils.hasText(keyword)) {
			users = userSvc.search(keyword, page, Config.resultPerAdminPage);
			model.addAttribute("keyword", keyword);
		} else
			users = userSvc.getAll(page, Config.resultPerAdminPage);
		
		model.addAllAttributes(Map.of(
			"titlePage", "Danh sách người dùng",
			"contentPage", "admin/user/list",
			"users", users,
			"totalPage", users.getTotalPages(),
			"currentPage", page
		));
		
		return "admin/index";
	}
	
	@GetMapping("/admin/user/delete/{id}")
	public String delete(RedirectAttributes redirectAttributes, @PathVariable int id) {
		User user = Optional.ofNullable(userSvc.getById(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		try {
			userSvc.delete(user);
			redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công");
		} catch (RuntimeException ex) {
			redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		}
		
		return "redirect:/admin/user";
	}
	
}
