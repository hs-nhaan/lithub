package com.hsnhaan.lithub.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public void handlerResponseStatusException(ResponseStatusException ex) {
		throw ex;
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public String handleConstraintViolationException(ConstraintViolationException ex, RedirectAttributes redirectAttributes, HttpServletRequest req) {
		redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		String referer = req.getHeader("Referer");
	    return "redirect:" + referer;
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public String handleIllegalArgumentException(IllegalArgumentException ex, RedirectAttributes redirectAttributes, HttpServletRequest req) {
		redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		String referer = req.getHeader("Referer");
	    return "redirect:" + referer;
	}
	
	@ExceptionHandler(DuplicateKeyException.class)
	public String handleDuplicateKeyException(DuplicateKeyException ex, RedirectAttributes redirectAttributes, HttpServletRequest req) {
		redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		String referer = req.getHeader("Referer");
	    return "redirect:" + referer;
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public String handleUsernameNotFoundException(UsernameNotFoundException ex, RedirectAttributes redirectAttributes, HttpServletRequest req) {
		redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		String referer = req.getHeader("Referer");
	    return "redirect:" + referer;
	}
	
	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes, HttpServletRequest req) {
		redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
		String referer = req.getHeader("Referer");
	    return "redirect:" + referer;
	}
	
}
