package com.hsnhaan.lithub.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hsnhaan.lithub.model.User;

public interface UserDAO extends JpaRepository<User, Integer> {

}
