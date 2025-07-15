package com.purrComplexity.TrabajoYa.Empleador;

import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Empleador {
    @Id
    @Column(unique = true, nullable = false)
    private String ruc;
    private String razonSocial;
    private Long telefonoPrincipal;

    @Email
    private String correo;

    @OneToMany(mappedBy = "empleador",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OfertaEmpleo> ofertas=new ArrayList<>() ;

    @OneToOne(mappedBy = "empresario", fetch = FetchType.LAZY)
    private UserAccount usuario;
}
