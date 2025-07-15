package com.purrComplexity.TrabajoYa.CalificacionTrabajador;

import com.purrComplexity.TrabajoYa.Contrato.Contrato;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
@Entity
public class CalificacionTrabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double puntuacionTrabajador;

    @OneToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;
}
