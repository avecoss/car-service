package dev.alexcoss.carservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "car model information")
public class CarModelDTO extends RepresentationModel<CarModelDTO> {
    private Long id;
    @NotNull
    @Schema(description = "car model name", example = "A3")
    private String name;
    @NotNull
    private ProducerDTO producer;
}
