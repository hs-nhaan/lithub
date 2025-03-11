package com.hsnhaan.lithub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.hsnhaan.lithub.model.User;

public interface IUserService {

	List<User> getAll();
	User getById(int id);
	Optional<User> getByEmail(String email);
	List<User> search(String keyword);
	Page<User> getAll(int page, int limit);
	Page<User> search(String keyword, int page, int limit);
	void save(User user);
	void update(User user);
	void delete(User user);
	boolean existsById(int id);
	
}
