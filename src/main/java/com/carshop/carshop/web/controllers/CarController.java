package com.carshop.carshop.web.controllers;

import com.carshop.carshop.domain.entities.Car;
import com.carshop.carshop.domain.models.binding.CarCreateBindingModel;
import com.carshop.carshop.domain.models.view.CarViewModel;
import com.carshop.carshop.domain.models.view.UserViewModel;
import com.carshop.carshop.service.CarService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cars")
public class CarController {

    private static final int CARS_PER_PAGE = 3;

    private final CarService carService;

    private final ModelMapper modelMapper;

    @Autowired
    public CarController(CarService carService, ModelMapper modelMapper) {
        this.carService = carService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create", produces = "application/json")
    public Map<String, String> create(CarCreateBindingModel bindingModel, @RequestParam(required = false) MultipartFile image, Principal principal) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("message", "success");
        if (BigDecimal.valueOf(bindingModel.getYearOfProduction()) == null) {
            map.put("message", "Please enter a valid year.");
            return map;
        }
        if (bindingModel.getYearOfProduction() == null || image == null
                || bindingModel.getMake().equals("") || bindingModel.getModel().equals("") || bindingModel.getPrice() == null) {
            map.put("message", "Please fill in all fields");
            return map;
        }

        if (!image.getOriginalFilename().endsWith(".jpg") && !image.getOriginalFilename().endsWith(".png")
                && !image.getOriginalFilename().endsWith(".jpeg")) {
            map.put("message", "Only the following formats are supported: jpg, jpeg and png.");
            return map;
        }

        this.carService.create(bindingModel, image, principal);

        return map;
    }

    @GetMapping(value = "/list", produces = "application/json")
    public List<CarViewModel> list(String make, String model, BigDecimal minPrice, BigDecimal maxPrice, Integer page) {

        if (page == null) page = 1;
        if (make == null) make = "";
        if (model == null) model = "";
        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = BigDecimal.valueOf(Long.MAX_VALUE);

        String finalModel = model;
        String finalMake = make;
        BigDecimal finalMinPrice = minPrice;
        BigDecimal finalMaxPrice = maxPrice;

        List<Car> cars = this.carService.findAll()
                .stream()
                .skip((page - 1) * CARS_PER_PAGE)
                .limit(CARS_PER_PAGE)
                .filter(x -> x.getModel().toLowerCase().contains(finalModel.toLowerCase()))
                .filter(x -> x.getMake().toLowerCase().contains(finalMake.toLowerCase()))
                .filter(x -> x.getPrice().compareTo(finalMinPrice) > 0 && x.getPrice().compareTo(finalMaxPrice) < 0)
                .collect(Collectors.toList());

        List<CarViewModel> carViewModels = new ArrayList<>();
        for (Car car : cars) {
            CarViewModel carViewModel = this.modelMapper.map(car, CarViewModel.class);
            carViewModel.setSeller(modelMapper.map(car.getSeller(), UserViewModel.class));

            carViewModels.add(carViewModel);
        }

        return carViewModels;
    }

    @GetMapping(value = "/pagesSize", produces = "application/json")
    public Map<String, Integer> getPageSizeInfo() {
        Map<String, Integer> map = new HashMap<>();
        map.put("pages", this.carService.findAll().size() / CARS_PER_PAGE);

        return map;
    }

    @GetMapping(value = "/details/{id}", produces = "application/json")
    public CarViewModel details(@PathVariable Integer id) {
        Car car = this.carService.findById(id);

        CarViewModel carViewModel = this.modelMapper.map(car, CarViewModel.class);
        UserViewModel userViewModel = this.modelMapper.map(car.getSeller(), UserViewModel.class);
        carViewModel.setSeller(userViewModel);

        return carViewModel;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/edit/{id}", produces = "application/json")
    public Map<String, String> edit(CarCreateBindingModel bindingModel, @RequestParam(required = false) MultipartFile image, @PathVariable Integer id, Principal principal) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("message", "success");

        if (BigDecimal.valueOf(bindingModel.getYearOfProduction()) == null) {
            map.put("message", "Please enter a valid year.");
            return map;
        }
        if (bindingModel.getYearOfProduction() == null
                || bindingModel.getMake().equals("") || bindingModel.getModel().equals("") || bindingModel.getPrice() == null) {
            map.put("message", "Please fill in all fields");
            return map;
        }

        if (image != null && !image.getOriginalFilename().endsWith(".jpg") && !image.getOriginalFilename().endsWith(".png")
                && !image.getOriginalFilename().endsWith(".jpeg")) {
            map.put("message", "Only the following formats are supported: jpg, jpeg and png.");
            return map;
        }

        if (!this.carService.edit(id, bindingModel, image, principal)) {
            map.put("message", "error");
            return map;
        }

        return map;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/delete/{id}", produces = "application/json")
    public Map<String, String> delete(@PathVariable Integer id, Principal principal) {
        Map<String, String> map = new HashMap<>();
        map.put("message", "success");

        if (!this.carService.delete(id, principal)) {
            map.put("message", "error");
            return map;
        }

        return map;
    }
}
