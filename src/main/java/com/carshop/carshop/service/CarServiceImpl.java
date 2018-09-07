package com.carshop.carshop.service;

import com.carshop.carshop.domain.entities.Car;
import com.carshop.carshop.domain.entities.User;
import com.carshop.carshop.domain.models.binding.CarCreateBindingModel;
import com.carshop.carshop.repository.CarRepository;
import com.carshop.carshop.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    private final CloudService cloudService;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, CloudService cloudService, UserRepository userRepository, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.cloudService = cloudService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void create(CarCreateBindingModel bindingModel, MultipartFile image, Principal principal) throws IOException {
        User user = this.userRepository.findByUsername(principal.getName());

        Car car = this.modelMapper.map(bindingModel, Car.class);
        String imageUrl = this.cloudService.upload(image);
        car.setImageUrl(imageUrl);

        car.setSeller(user);

        this.carRepository.saveAndFlush(car);
    }

    @Override
    public boolean edit(Integer id, CarCreateBindingModel bindingModel, MultipartFile image, Principal principal) throws IOException {
        User user = this.userRepository.findByUsername(principal.getName());
        Optional<Car> carOptional = this.carRepository.findById(id);
        if (!carOptional.isPresent()) return false;
        Car car = carOptional.get();
        if (!user.isSeller(car)) return false;

        car.setCountry(bindingModel.getCountry());
        car.setMake(bindingModel.getMake());
        car.setModel(bindingModel.getModel());
        car.setYearOfProduction(bindingModel.getYearOfProduction());
        car.setPrice(bindingModel.getPrice());

        if (image != null) {
            car.setImageUrl(this.cloudService.upload(image));
        }

        this.carRepository.save(car);

        return true;
    }

    @Override
    public boolean delete(Integer id, Principal principal) {
        User user = this.userRepository.findByUsername(principal.getName());
        Optional<Car> carOptional = this.carRepository.findById(id);
        if (!carOptional.isPresent()) return false;
        Car car = carOptional.get();
        if (!user.isSeller(car)) return false;

        this.carRepository.deleteById(id);

        return true;
    }

    @Override
    public List<Car> findAll() {
        return this.carRepository.findAll();
    }

    @Override
    public Car findById(Integer id) {
        return this.carRepository.findById(id).get();
    }
}
