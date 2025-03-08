package com.hsnhaan.lithub.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hsnhaan.lithub.model.Rating;

public interface RatingDAO extends JpaRepository<Rating, Integer> {

}
