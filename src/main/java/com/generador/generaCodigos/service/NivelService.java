package com.generador.generaCodigos.service;

import com.generador.generaCodigos.model.Nivel;
import com.generador.generaCodigos.repository.NivelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NivelService {

    private final NivelRepository nivelRepository;

    public NivelService(NivelRepository nivelRepository) {
        this.nivelRepository = nivelRepository;
    }

    public List<Nivel> obtenerTodosLosNiveles() {
        return nivelRepository.findAll();
    }

    public List<Nivel> obtenerNivelesPorPadre(Integer nivelPadreId) {
        return nivelRepository.findByNivelPadreId(nivelPadreId);
    }

    // ✅ Nuevo método para obtener niveles raíz (sin padre)
    public List<Nivel> obtenerNivelesRaiz() {
        try {
            return nivelRepository.findByNivelPadreIsNull();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener niveles raíz: " + e.getMessage());
            return List.of(); // ✅ Devuelve una lista vacía en lugar de romperse
        }
    }


    public Optional<Nivel> obtenerNivelPorId(Integer id) {
        return nivelRepository.findById(id);
    }

    public Nivel guardarNivel(Nivel nivel) {
        return nivelRepository.save(nivel);
    }

    public void eliminarNivel(Integer id) {
        nivelRepository.deleteById(id);
    }

    public String obtenerRutaPadres(Nivel nivel) {
        List<String> ruta = new ArrayList<>();

        while (nivel != null) {
            ruta.add(0, nivel.getNombre()); // ✅ Agregar al inicio para mantener el orden jerárquico
            nivel = nivel.getNivelPadre();  // ✅ Subir al nivel padre
        }

        return String.join(" / ", ruta); // ✅ Retorna la ruta en formato "Raíz / Subnivel1 / Subnivel2"
    }
}
