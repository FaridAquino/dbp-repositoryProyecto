package com.purrComplexity.TrabajoYa.DTO;

import lombok.Data;

@Data
public class ProfileDTO {
    private Boolean hasEmpleadorProfile;
    private Boolean hasTrabajadorProfile;
    private String  empleadorRuc;
    private Long trabajadorId;
    private String empleadorRazonSocial;
    private String trabajadorNombres;
}
