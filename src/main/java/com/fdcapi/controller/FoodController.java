package com.fdcapi.controller;

import com.fdcapi.model.Food;
import com.fdcapi.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar operaciones relacionadas con alimentos
 * utilizando la API de Food Data Central (FDC)
 */
@RestController
@RequestMapping("/api/foods")
@Slf4j
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    /**
     * Busca alimentos por nombre o coincidencias parciales
     * 
     * @param query Término de búsqueda (nombre del alimento)
     * @return Lista de alimentos que coinciden con la búsqueda
     * 
     * Ejemplo: GET /api/foods?query=apple
     */
    @GetMapping
    public ResponseEntity<List<Food>> searchFoods(@RequestParam String query) {
        log.info("Petición recibida para buscar alimentos: {}", query);
        List<Food> foods = foodService.searchFoods(query);
        
        if (foods.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(foods);
    }

    /**
     * Obtiene información detallada de un alimento por su ID de FDC
     * 
     * @param id ID del alimento en Food Data Central
     * @return Información nutricional completa del alimento
     * 
     * Ejemplo: GET /api/foods/123456
     */
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long id) {
        log.info("Petición recibida para obtener alimento con ID: {}", id);
        Food food = foodService.getFoodById(id);
        return ResponseEntity.ok(food);
    }

    /**
     * Endpoint de prueba para verificar que el servicio está activo
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("FDC API Service is running");
    }
}