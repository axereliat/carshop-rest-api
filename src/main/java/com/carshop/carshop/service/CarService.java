package com.carshop.carshop.service;

import com.carshop.carshop.domain.entities.Car;
import com.carshop.carshop.domain.models.binding.CarCreateBindingModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface CarService {

    void create(CarCreateBindingModel bindingModel, MultipartFile image, Principal principal) throws IOException;

    List<Car> findAll();

    Car findById(Integer id);

    boolean edit(Integer id, CarCreateBindingModel bindingModel, MultipartFile image, Principal principal) throws IOException;

    boolean delete(Integer id, Principal principal);
}
