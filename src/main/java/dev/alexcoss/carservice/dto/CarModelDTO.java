package dev.alexcoss.carservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class CarModelDTO extends RepresentationModel<CarModelDTO> {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private ProducerDTO producer;
}
