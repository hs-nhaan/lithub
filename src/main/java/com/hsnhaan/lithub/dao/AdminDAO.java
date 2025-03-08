package com.hsnhaan.lithub.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hsnhaan.lithub.model.Admin;

public interface AdminDAO extends JpaRepository<Admin, Integer> {

}
