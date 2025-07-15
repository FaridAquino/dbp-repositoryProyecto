package com.purrComplexity.TrabajoYa.HorarioDia;

import com.purrComplexity.TrabajoYa.Enum.WeekDays;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class HorarioDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private WeekDays weekDays;

    private Integer horas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ofertaEmpleo_id")
    private OfertaEmpleo ofertaEmpleo;

}
