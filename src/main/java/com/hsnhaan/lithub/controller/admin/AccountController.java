package com.hsnhaan.lithub.controller.admin;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hsnhaan.lithub.model.Admin;
import com.hsnhaan.lithub.service.Implement.AdminServiceImpl;
import com.hsnhaan.lithub.util.Config;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AccountController {

	private final AdminServiceImpl adminSvc;
	
	public AccountController(AdminServiceImpl adminSvc) {
		this.adminSvc = adminSvc;
	}
	
	@GetMapping("/admin/account")
	public String list(Model model, @RequestParam(defaultValue = "1") int page, 
					@RequestParam(required = false) String keyword) {
		page = page < 1 ? 1 : page;
		Page<Admin> admins;
		if (StringUtils.hasText(keyword)) {
			admins = adminSvc.search(keyword, page, Config.resultOnAdminPage);
			model.addAttribute("keyword", keyword);
		}
		else
			admins = adminSvc.getAll(page, Config.resultOnAdminPage);
		model.addAllAttributes(Map.of(
			"titlePage", "Danh sách tài khoản",
			"contentPage", "admin/account/list",
			"admins", admins,
			"totalPage", admins.getTotalPages(),
			"currentPage", page
		));
		return "admin/index";
	}
	
	@GetMapping("/admin/account/add")
	public String add(Model model) {
		model.addAllAttributes(Map.of(
			"titlePage", "Thêm tài khoản",
			"contentPage", "admin/account/add"
		));
		return "admin/index";
	}
	
	@GetMapping("/admin/account/edit/{username}")
	public String edit(Model model, @PathVariable String username) {
		Admin admin = adminSvc.getByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAllAttributes(Map.of(
			"titlePage", "Chỉnh sửa tài khoản",
			"contentPage", "admin/account/edit",
			"admin", admin
		));
		return "admin/index";
	}
	
	@PostMapping("/admin/account/create")
	public String create(RedirectAttributes redirectAttributes, 
			@ModelAttribute Admin admin, @RequestParam(required = false) String rePassword) {
		adminSvc.save(admin, rePassword);
		redirectAttributes.addFlashAttribute("success", "Thêm tài khoản thành công");
	    return "redirect:/admin/account/add";
	}
	
	@PatchMapping("/admin/account/update/{username}")
	public String update(RedirectAttributes redirectAttributes, @PathVariable String username, 
			@RequestParam String full_name) {
		adminSvc.update(username, full_name);
		redirectAttributes.addFlashAttribute("success", "Chỉnh sửa tài khoản thành công");
		return "redirect:/admin/account/edit/" + username;
	}
	
	@DeleteMapping("/admin/account/delete/{username}")
	public String delete(RedirectAttributes redirectAttributes, HttpServletRequest req, 
			@PathVariable String username) {
		adminSvc.delete(username);
		redirectAttributes.addFlashAttribute("success", "Xóa tài khoản thành công");
		return "redirect:" + req.getHeader("Referer");
	}
	
}
