package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.CarModelDTO;
import dev.alexcoss.carservice.model.CarModel;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.CarModelRepository;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import dev.alexcoss.carservice.util.exception.IllegalModelException;
import dev.alexcoss.carservice.util.exception.IllegalProducerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final ModelMapper modelMapper;

    public CarModelDTO getCarModelById(Long id) {
        CarModel carModel = carModelRepository.findById(id)
            .orElseThrow(() -> getEntityNotExistException(id));
        return modelMapper.map(carModel, CarModelDTO.class);
    }

    @Transactional
    public CarModelDTO createCarModel(CarModelDTO carModelDTO) {
        isValidCarModel(carModelDTO);
        CarModel savedCarModel = carModelRepository.save(modelMapper.map(carModelDTO, CarModel.class));
        return modelMapper.map(savedCarModel, CarModelDTO.class);
    }

    @Transactional
    public CarModelDTO updateCarModel(CarModelDTO carModelDTO) {
        isValidCarModel(carModelDTO);
        CarModel existingCarModel = carModelRepository.findById(carModelDTO.getId())
            .orElseThrow(() -> getEntityNotExistException(carModelDTO.getId()));

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(carModelDTO, existingCarModel);

        CarModel savedCarModel = carModelRepository.save(existingCarModel);
        return modelMapper.map(savedCarModel, CarModelDTO.class);
    }

    public Page<CarModelDTO> getListCarModels(String producerName, Pageable pageable) {
        if (producerName == null || producerName.isBlank()) {
            return carModelRepository.findAll(pageable)
                .map(pageModel -> modelMapper.map(pageModel, CarModelDTO.class));
        } else {
            return carModelRepository.findByProducerName(producerName, pageable)
                .map(pageModel -> modelMapper.map(pageModel, CarModelDTO.class));
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCarModel(Long id) {
        CarModel existingCarModel = carModelRepository.findById(id)
            .orElseThrow(() -> getEntityNotExistException(id));
        carModelRepository.delete(existingCarModel);
    }

    private EntityNotExistException getEntityNotExistException(Long id) {
        log.error("Cannot find model with id {}", id);
        return new EntityNotExistException("Car model: " + id + " not found");
    }

    private void isValidCarModel(CarModelDTO carModel) {
        if (carModel.getName() == null || carModel.getName().isBlank()) {
            throw new IllegalModelException("Model: " + carModel.getName() + " not found");
        }
        if (carModel.getProducer() == null || carModel.getProducer().getName().isBlank()) {
            throw new IllegalProducerException("Producer: " + carModel.getProducer() + " not found");
        }
    }
}
