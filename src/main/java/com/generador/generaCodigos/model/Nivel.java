package com.generador.generaCodigos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "niveles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Nivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "nivel_padre_id")
    @JsonIgnoreProperties("subniveles") // Permite la serialización de `nivelPadre`, pero evita recursión infinita
    private Nivel nivelPadre;

    @OneToMany(mappedBy = "nivelPadre", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("nivelPadre") // Evita ciclos de referencia infinita

    private List<Nivel> subniveles;

}
