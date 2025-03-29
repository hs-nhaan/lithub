package com.hsnhaan.lithub.controller.admin;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.User;
import com.hsnhaan.lithub.service.Implement.UserServiceImpl;
import com.hsnhaan.lithub.util.Config;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

	private final UserServiceImpl userSvc;
	
	public UserController(UserServiceImpl userSvc) {
		this.userSvc = userSvc;
	}
	
	@GetMapping("/admin/user")
	public String list(Model model, @RequestParam(defaultValue = "1") int page,
						@RequestParam(required = false) String keyword) {
		page = page < 1 ? 1 : page;
		Page<User> users;
		if (StringUtils.hasText(keyword)) {
			users = userSvc.search(keyword, page, Config.resultOnAdminPage);
			model.addAttribute("keyword", keyword);
		} else
			users = userSvc.getAll(page, Config.resultOnAdminPage);
		
		model.addAllAttributes(Map.of(
			"titlePage", "Danh sách người dùng",
			"contentPage", "admin/user/list",
			"users", users,
			"totalPage", users.getTotalPages(),
			"currentPage", page
		));
		
		return "admin/index";
	}
	
	@DeleteMapping("/admin/user/delete/{id}")
	public String delete(HttpServletRequest req, RedirectAttributes redirectAttributes, @PathVariable int id) {
		userSvc.delete(id);
		redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công");
	    return "redirect:" + req.getHeader("Referer");
	}
	
}
