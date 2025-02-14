package com.generador.generaCodigos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "niveles")  // ✅ Ahora buscará "niveles" en PostgreSQL
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Nivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String codigo;
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "nivel_padre_id")
    //@JsonBackReference // ✅ Evita referencias circulares en la serialización JSON
    @JsonIgnoreProperties("subniveles") // ✅ Permite la serialización de `nivelPadre`, pero evita recursión infinita
    private Nivel nivelPadre;

    @OneToMany(mappedBy = "nivelPadre", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonManagedReference // ✅ Permite serializar correctamente los subniveles
    @JsonIgnoreProperties("nivelPadre") // ✅ Evita ciclos de referencia infinita

    private List<Nivel> subniveles;

}
