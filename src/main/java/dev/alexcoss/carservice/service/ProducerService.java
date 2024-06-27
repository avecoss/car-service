package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.*;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import dev.alexcoss.carservice.util.exception.IllegalEntityException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProducerService {

    private final ProducerRepository producerRepository;
    private final ModelMapper modelMapper;

    public Page<ProducerDTO> getListOfProducers(Pageable pageable) {
        return producerRepository.findAll(pageable)
            .map(producer -> modelMapper.map(producer, ProducerDTO.class));
    }

    @Transactional
    public ProducerDTO createProducer(ProducerDTO producerDTO) {
        isValidProducer(producerDTO);
        Producer savedProducer = producerRepository.save(modelMapper.map(producerDTO, Producer.class));
        return modelMapper.map(savedProducer, ProducerDTO.class);
    }

    @Transactional
    public ProducerDTO updateProducer(String currentName, ProducerDTO producerDTO) {
        isValidProducer(producerDTO);
        Producer existingProducer = producerRepository.findByName(currentName)
            .orElseThrow(() -> new EntityNotExistException("Producer: " + currentName + " not found"));
        existingProducer.setName(producerDTO.getName());

        Producer updatedProducer = producerRepository.save(existingProducer);
        return modelMapper.map(updatedProducer, ProducerDTO.class);
    }

    @Transactional
    public void deleteProducer(String name) {
        Producer producer = producerRepository.findByName(name)
            .orElseThrow(() -> new EntityNotExistException("Producer: " + name + " not found"));
        producerRepository.delete(producer);
    }

    private void isValidProducer(ProducerDTO producerDTO) {
        if (producerDTO.getName() == null || producerDTO.getName().isBlank()) {
            throw new IllegalEntityException("Producer: " + producerDTO.getName() + " not found");
        }
    }
}
