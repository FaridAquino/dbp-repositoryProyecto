package com.purrComplexity.TrabajoYa.Contrato;

import com.purrComplexity.TrabajoYa.CalificacionEmpresa.CalificacionEmpresa;
import com.purrComplexity.TrabajoYa.CalificacionTrabajador.CalificacionTrabajador;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import com.purrComplexity.TrabajoYa.Trabajador.Trabajador;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "trabajadorContratado_id")
    private Trabajador trabajadorContratado;

    @ManyToOne
    @JoinColumn(name = "ofertaEmpleo_id")
    private OfertaEmpleo ofertaEmpleo;

    @OneToOne(mappedBy = "contrato")
    private CalificacionTrabajador calificacionTrabajador;

    @OneToOne(mappedBy = "contrato")
    private CalificacionEmpresa calificacionEmpresa;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double calificacionPromedio = 0.0;


}
