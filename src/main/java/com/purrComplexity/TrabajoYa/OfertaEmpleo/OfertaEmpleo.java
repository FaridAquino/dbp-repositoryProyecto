package com.purrComplexity.TrabajoYa.OfertaEmpleo;

import com.purrComplexity.TrabajoYa.Contrato.Contrato;
import com.purrComplexity.TrabajoYa.Empleador.Empleador;
import com.purrComplexity.TrabajoYa.Enum.WeekDays;
import com.purrComplexity.TrabajoYa.Enum.SistemaRemuneracion;
import com.purrComplexity.TrabajoYa.Enum.modalidad;
import com.purrComplexity.TrabajoYa.HorarioDia.HorarioDia;
import com.purrComplexity.TrabajoYa.Trabajador.Trabajador;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OfertaEmpleo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOfertaEmpleo;

    private String periodoPago;

    private Long montoPorPeriodo;

    @Enumerated(EnumType.STRING)
    private modalidad modalidadEmpleo; // VIRTUAL, PRESENCIAL, HIBRIDO

    private Double longitud;

    private Double latitud;

    private String habilidades;

    @NotNull
    private Integer numeroPostulaciones;

    @URL(message = "La URL  de la imagen no es válida")
    private String imagen;

    private LocalDateTime fechaLimite;

    private Boolean isDisponible=true;

    private String Puesto;

    private String FuncionesPuesto;

    @OneToMany(mappedBy = "ofertaEmpleo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contrato> contratos =new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "empleador_id")
    private Empleador empleador;

    @ManyToMany(mappedBy = "postulaste")
    private List<Trabajador> postulantes =new ArrayList<>();

    @ManyToMany(mappedBy = "contratado")
    private List<Trabajador> contratados =new ArrayList<>();

    @OneToMany(mappedBy = "ofertaEmpleo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HorarioDia> horarioDias=new ArrayList<>();

};
