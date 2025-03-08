package com.hsnhaan.lithub.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hsnhaan.lithub.model.Comment;

public interface CommentDAO extends JpaRepository<Comment, Integer> {

}
