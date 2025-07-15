package com.purrComplexity.TrabajoYa.Trabajador;

import com.purrComplexity.TrabajoYa.Contrato.Contrato;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;

@Data
@Entity
public class Trabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private String ubicacion;
    private Double longitud;
    private Double latitud;

    @Column(unique = true)
    @Email
    private String correo;
    private Date fechaNacimiento;
// ubicacion, correo, fecha de nacimient, habilidad, dni, nombresCompletos, calificaciones, contratos,

    private String habilidades;

    private String dni;
    private String nombresCompletos;

    //Relaciones
    @OneToMany(mappedBy = "trabajadorContratado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contrato> contratos;

    @ManyToMany
    @JoinTable(
            name = "persona_postulacion",
            joinColumns = @JoinColumn(name = "persona_id"),
            inverseJoinColumns = @JoinColumn(name = "oferta_id")
    )
    private List<OfertaEmpleo> postulaste =new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "persona_contratado",
            joinColumns = @JoinColumn(name = "persona_id"),
            inverseJoinColumns = @JoinColumn(name = "oferta_id")
    )
    private List<OfertaEmpleo> contratado=new ArrayList<>();

    @OneToOne(mappedBy = "trabajador",fetch = FetchType.LAZY)
    private UserAccount usuario;

}
