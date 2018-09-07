package com.carshop.carshop.service;

import com.carshop.carshop.domain.entities.Car;
import com.carshop.carshop.domain.entities.Comment;
import com.carshop.carshop.domain.entities.User;
import com.carshop.carshop.repository.CarRepository;
import com.carshop.carshop.repository.CommentRepository;
import com.carshop.carshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final CarRepository carRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, CarRepository carRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @Override
    public Comment create(String content, Integer carId, Principal principal) {
        Optional<Car> carOptional = this.carRepository.findById(carId);
        if (!carOptional.isPresent()) return null;

        Car car = carOptional.get();
        User user = this.userRepository.findByUsername(principal.getName());

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(user);
        comment.setCar(car);

        return this.commentRepository.saveAndFlush(comment);
    }

    @Override
    public Comment findById(Integer id) {
        return this.commentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Integer id) {
        this.commentRepository.deleteById(id);
    }
}
