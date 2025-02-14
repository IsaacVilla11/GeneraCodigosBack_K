package com.generador.generaCodigos.repository;

import com.generador.generaCodigos.model.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NivelRepository extends JpaRepository<Nivel, Integer> {

    // ✅ Método para encontrar niveles por ID de padre
    List<Nivel> findByNivelPadreId(Integer nivelPadreId);

    // ✅ Método para encontrar los niveles raíz (sin padre)
    List<Nivel> findByNivelPadreIsNull();

    @Query(value = """
    WITH RECURSIVE NivelRecursivo AS (
        SELECT id, nivel_padre_id, 1 AS profundidad
        FROM niveles
        WHERE nivel_padre_id IS NULL
        
        UNION ALL
        
        SELECT n.id, n.nivel_padre_id, nr.profundidad + 1
        FROM niveles n
        INNER JOIN NivelRecursivo nr ON n.nivel_padre_id = nr.id
    )
    SELECT MAX(profundidad) FROM NivelRecursivo
""", nativeQuery = true)
    int obtenerCantidadMaximaNiveles();



}
