package com.carshop.carshop.web.controllers;

import com.carshop.carshop.domain.entities.Car;
import com.carshop.carshop.domain.entities.Comment;
import com.carshop.carshop.domain.entities.User;
import com.carshop.carshop.domain.models.binding.CommentCreateBindingModel;
import com.carshop.carshop.domain.models.view.CommentViewModel;
import com.carshop.carshop.domain.models.view.UserViewModel;
import com.carshop.carshop.service.CarService;
import com.carshop.carshop.service.CommentService;
import com.carshop.carshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    
    private final CarService carService;

    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, CarService carService, UserService userService) {
        this.commentService = commentService;
        this.carService = carService;
        this.userService = userService;
    }

    @PostMapping("/create/{carId}")
    public CommentViewModel create(@RequestBody CommentCreateBindingModel bindingModel, @PathVariable Integer carId, Principal principal) {
        Comment comment = this.commentService.create(bindingModel.getContent(), carId, principal);

        CommentViewModel commentViewModel = new CommentViewModel();
        commentViewModel.setAddedOn(comment.getAddedOn().toString());
        commentViewModel.setContent(comment.getContent());

        UserViewModel userViewModel = new UserViewModel();
        userViewModel.setId(comment.getAuthor().getId());
        userViewModel.setUsername(comment.getAuthor().getUsername());

        commentViewModel.setAuthor(userViewModel);
        comment.setId(comment.getId());

        return commentViewModel;
    }
    
    @GetMapping("/list/{carId}")
    public List<CommentViewModel> list(@PathVariable Integer carId) {
        Car car = this.carService.findById(carId);

        List<CommentViewModel> commentViewModels = new ArrayList<>();
        for (Comment comment : car.getComments().stream()
                .sorted((x1, x2) -> x2.getAddedOn().compareTo(x1.getAddedOn()))
                .collect(Collectors.toList())) {
            CommentViewModel commentViewModel = new CommentViewModel();
            commentViewModel.setContent(comment.getContent());
            UserViewModel userViewModel = new UserViewModel();
            userViewModel.setId(comment.getAuthor().getId());
            userViewModel.setUsername(comment.getAuthor().getUsername());
            commentViewModel.setAuthor(userViewModel);
            commentViewModel.setAddedOn(comment.getAddedOn().toString());
            commentViewModel.setId(comment.getId());

            commentViewModels.add(commentViewModel);
        }

        return commentViewModels;
    }

    @GetMapping("/delete/{id}")
    public Map<String, String> delete(@PathVariable Integer id, Principal principal) {
        Comment comment = this.commentService.findById(id);
        User user = this.userService.findByUsername(principal.getName());
        if (!user.hasWrittenComment(comment)) return new HashMap<>();

        this.commentService.deleteById(id);
        return new HashMap<>();
    }
}
