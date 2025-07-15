package com.purrComplexity.TrabajoYa.CalificacionEmpresa;

import com.purrComplexity.TrabajoYa.Contrato.Contrato;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Entity
@Data
public class CalificacionEmpresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double puntuacionEmpresa;

    @OneToOne
    @JoinColumn(name = "contrato_id")
    private Contrato contrato;
}
