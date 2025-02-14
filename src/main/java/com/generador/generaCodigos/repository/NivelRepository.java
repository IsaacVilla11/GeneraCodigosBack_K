package com.generador.generaCodigos.repository;

import com.generador.generaCodigos.model.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NivelRepository extends JpaRepository<Nivel, Integer> {

    // ✅ Método para encontrar niveles por ID de padre
    List<Nivel> findByNivelPadreId(Integer nivelPadreId);

    // ✅ Método para encontrar los niveles raíz (sin padre)
    List<Nivel> findByNivelPadreIsNull();
}
