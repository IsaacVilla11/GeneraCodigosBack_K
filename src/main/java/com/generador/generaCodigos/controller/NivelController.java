package com.generador.generaCodigos.controller;

import com.generador.generaCodigos.model.Nivel;
import com.generador.generaCodigos.service.NivelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/niveles")
@CrossOrigin(origins = "http://localhost:3000")
public class NivelController {

    private final NivelService nivelService;

    public NivelController(NivelService nivelService) {
        this.nivelService = nivelService;
    }

    // ✅ Obtiene todos los niveles
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerTodosLosNiveles() {
        List<Nivel> niveles = nivelService.obtenerTodosLosNiveles();

        // Convertir la lista para incluir el nivel padre en la respuesta
        List<Map<String, Object>> response = niveles.stream().map(nivel -> {
            Map<String, Object> nivelMap = new HashMap<>();
            nivelMap.put("id", nivel.getId());
            nivelMap.put("codigo", nivel.getCodigo());
            nivelMap.put("nombre", nivel.getNombre());

            // Agregar el nivel padre si existe
            if (nivel.getNivelPadre() != null) {
                Map<String, Object> padreMap = new HashMap<>();
                padreMap.put("id", nivel.getNivelPadre().getId());
                padreMap.put("codigo", nivel.getNivelPadre().getCodigo());
                padreMap.put("nombre", nivel.getNivelPadre().getNombre());
                nivelMap.put("nivelPadre", padreMap);
            } else {
                nivelMap.put("nivelPadre", null);
            }

            return nivelMap;
        }).toList();

        return ResponseEntity.ok(response);
    }

    // ✅ Obtiene los niveles que tienen un padre específico
    @GetMapping("/padre")
    public ResponseEntity<?> obtenerNivelesPorPadre(@RequestParam(required = false) Integer nivelPadreId) {
        try {
            List<Nivel> niveles;
            if (nivelPadreId == null) {
                niveles = nivelService.obtenerNivelesRaiz();
            } else {
                niveles = nivelService.obtenerNivelesPorPadre(nivelPadreId);
            }
            return ResponseEntity.ok(niveles);
        } catch (Exception e) {
            //System.err.println("❌ Error en obtenerNivelesPorPadre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno en el servidor");
        }
    }


    // ✅ Obtiene un nivel por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Nivel> obtenerNivelPorId(@PathVariable Integer id) {
        Optional<Nivel> nivel = nivelService.obtenerNivelPorId(id);
        if (nivel.isPresent()) {
            // 📌 Verificar si el nivel padre se está enviando completo
            //System.out.println("📌 Nivel obtenido: " + nivel.get());
        }
        return nivel.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/jerarquia/{id}")
    public ResponseEntity<Map<String, Object>> obtenerJerarquiaNivel(@PathVariable Integer id) {
        Optional<Nivel> nivelOpt = nivelService.obtenerNivelPorId(id);
        if (nivelOpt.isPresent()) {
            Nivel nivel = nivelOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("nivel", nivel);
            response.put("rutaCompleta", nivelService.obtenerRutaPadres(nivel));

            // ✅ Cargar subniveles correctamente
            List<Nivel> subniveles = nivelService.obtenerNivelesPorPadre(nivel.getId());
            response.put("subniveles", subniveles);

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/cantidadMaxima")
    public ResponseEntity<Integer> obtenerCantidadMaximaNiveles() {
        int cantidadMaxima = nivelService.obtenerCantidadMaximaNiveles();
        return ResponseEntity.ok(cantidadMaxima);
    }



    // ✅ Crea un nuevo nivel
    @PostMapping
    public ResponseEntity<Nivel> crearNivel(@RequestBody Nivel nivel) {
        System.out.println("📥 Recibido: " + nivel.getCodigo() + ", Nivel Padre ID: " +
                (nivel.getNivelPadre() != null ? nivel.getNivelPadre().getId() : "NULL"));

        if (nivel.getNivelPadre() != null) {
            Optional<Nivel> padre = nivelService.obtenerNivelPorId(nivel.getNivelPadre().getId());
            nivel.setNivelPadre(padre.orElse(null)); // ✅ Asigna el padre solo si existe
        }

        Nivel nuevoNivel = nivelService.guardarNivel(nivel);
        System.out.println("✅ Nuevo nivel creado: " + nuevoNivel.getNombre() + " con padre: " +
                (nuevoNivel.getNivelPadre() != null ? nuevoNivel.getNivelPadre().getNombre() : "Raíz"));

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoNivel);
    }

    // ✅ Actualiza un nivel existente
    @PutMapping("/{id}")
    public ResponseEntity<Nivel> actualizarNivel(@PathVariable Integer id, @RequestBody Nivel nivelActualizado) {
        Optional<Nivel> optionalNivel = nivelService.obtenerNivelPorId(id);
        if (optionalNivel.isPresent()) {
            Nivel nivel = optionalNivel.get();
            nivel.setNombre(nivelActualizado.getNombre());
            nivel.setCodigo(nivelActualizado.getCodigo());
            nivel.setNivelPadre(nivelActualizado.getNivelPadre() != null ?
                    nivelService.obtenerNivelPorId(nivelActualizado.getNivelPadre().getId()).orElse(null) : null);
            nivelService.guardarNivel(nivel);
            return ResponseEntity.ok(nivel);
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ Elimina un nivel por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNivel(@PathVariable Integer id) {
        nivelService.eliminarNivel(id);
        return ResponseEntity.noContent().build();
    }
}
