package dev.alexcoss.carservice.service;

import dev.alexcoss.carservice.dto.ProducerDTO;
import dev.alexcoss.carservice.model.Producer;
import dev.alexcoss.carservice.repository.ProducerRepository;
import dev.alexcoss.carservice.util.exception.EntityNotExistException;
import dev.alexcoss.carservice.util.exception.IllegalProducerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProducerService {

    private final ProducerRepository producerRepository;
    private final ModelMapper modelMapper;

    public ProducerDTO getProducerById(Long id) {
        Producer producer = producerRepository.findById(id)
            .orElseThrow(() -> getEntityNotExistException(id));
        return modelMapper.map(producer, ProducerDTO.class);
    }

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
    public ProducerDTO updateProducer(ProducerDTO producerDTO) {
        isValidProducer(producerDTO);
        Producer existingProducer = producerRepository.findById(producerDTO.getId())
            .orElseThrow(() -> getEntityNotExistException(producerDTO.getId()));

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(producerDTO, existingProducer);

        Producer updatedProducer = producerRepository.save(existingProducer);
        return modelMapper.map(updatedProducer, ProducerDTO.class);
    }

    @Transactional
    public void deleteProducer(Long id) {
        Producer producer = producerRepository.findById(id)
            .orElseThrow(() -> getEntityNotExistException(id));
        producerRepository.delete(producer);
    }

    private EntityNotExistException getEntityNotExistException(Long id) {
        log.error("Producer with id {} does not exist", id);
        return new EntityNotExistException("Producer: " + id + " not found");
    }

    private void isValidProducer(ProducerDTO producerDTO) {
        if (producerDTO.getName() == null || producerDTO.getName().isBlank()) {
            throw new IllegalProducerException("Producer: " + producerDTO.getName() + " cannot be empty");
        }
    }
}
