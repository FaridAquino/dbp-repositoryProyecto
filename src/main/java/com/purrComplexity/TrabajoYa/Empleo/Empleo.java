package com.purrComplexity.TrabajoYa.Empleo;

import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Empleo extends OfertaEmpleo {

    private String Puesto;
    private String FuncionesPuesto;

}
