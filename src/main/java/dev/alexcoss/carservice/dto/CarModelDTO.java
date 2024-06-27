package dev.alexcoss.carservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModelDTO {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private ProducerDTO producer;
}
