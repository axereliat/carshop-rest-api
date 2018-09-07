package com.carshop.carshop.service;

import com.carshop.carshop.domain.entities.Comment;

import java.security.Principal;

public interface CommentService {

    Comment create(String content, Integer carId, Principal principal);

    Comment findById(Integer id);

    void deleteById(Integer id);
}
