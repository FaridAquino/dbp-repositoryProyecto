package com.purrComplexity.TrabajoYa.Empleador.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmpleadorDTO {
    private String ruc;
    private String razonSocial;
    private Long telefonoPrincipal;
    @Email
    private String correo;
}
