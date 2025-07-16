package com.purrComplexity.TrabajoYa.DTO;

import lombok.Data;

@Data
public class AceptadoDTO {
    private String trabajadorNombreCompleto;
    private Long trabajadorId;
    private String empleadorRazonSocial;
    private String empleadorRuc;
    private String empleadorCorreo;
    private Long ofertaEmpleoId;
    private Double ofertaEmpleoLatitud;
    private Double ofertaEmpleoLongitud;

}
