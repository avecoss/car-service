package dev.alexcoss.carservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class ProducerDTO extends RepresentationModel<ProducerDTO> {
    private Long id;
    @NotNull
    private String name;
}
