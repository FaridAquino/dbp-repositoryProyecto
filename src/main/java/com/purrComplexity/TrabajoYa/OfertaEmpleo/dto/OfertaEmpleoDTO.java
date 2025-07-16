package com.purrComplexity.TrabajoYa.OfertaEmpleo.dto;

import com.purrComplexity.TrabajoYa.Enum.SistemaRemuneracion;
import com.purrComplexity.TrabajoYa.Enum.WeekDays;
import com.purrComplexity.TrabajoYa.Enum.modalidad;
import com.purrComplexity.TrabajoYa.HorarioDia.dto.CreatehorarioDiaDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OfertaEmpleoDTO {

    private Long idOfertaEmpleo;

    private String periodoPago;

    private Long montoPorPeriodo;

    @Enumerated(EnumType.STRING)
    private modalidad modalidadEmpleo; // VIRTUAL, PRESENCIAL, HIBRIDO

    private Double longitud;

    private Double latitud;

    private String habilidades;

    private Long numeroPostulaciones;

    @URL(message = "La URL  de la imagen no es válida")
    private String imagen;

    private LocalDateTime fechaLimite;

    private String puesto;

    private String funcionesPuesto;

    private String razonSocial;

    private List<CreatehorarioDiaDTO> horarioDias;


}
