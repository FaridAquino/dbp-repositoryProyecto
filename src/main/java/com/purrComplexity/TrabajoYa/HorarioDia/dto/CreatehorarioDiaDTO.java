package com.purrComplexity.TrabajoYa.HorarioDia.dto;

import com.purrComplexity.TrabajoYa.Enum.WeekDays;
import lombok.Data;

@Data
public class CreatehorarioDiaDTO {
    private WeekDays weekDays;
    private Integer horas;
}
