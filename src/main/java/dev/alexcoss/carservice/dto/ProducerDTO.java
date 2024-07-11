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
@Schema(description = "manufacturer information")
public class ProducerDTO extends RepresentationModel<ProducerDTO> {
    @Schema(hidden = true)
    private Long id;

    @NotNull
    @Schema(description = "name of manufacturer", example = "Audi")
    private String name;
}
