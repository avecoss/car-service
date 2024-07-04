package dev.alexcoss.carservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(schema = "car", name = "producer")
@Builder
public class Producer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producer_id")
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String name;
}
