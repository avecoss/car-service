package dev.alexcoss.carservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarFilterDTO {
    private String manufacturer;
    private String model;
    private Integer minYear;
    private Integer maxYear;
    private String category;
    private Pageable pageable;
}
