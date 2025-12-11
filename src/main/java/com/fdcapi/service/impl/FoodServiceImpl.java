package com.fdcapi.service.impl;

import com.fdcapi.client.FdcClient;
import com.fdcapi.client.dto.FdcFoodItem;
import com.fdcapi.client.dto.FdcNutrient;
import com.fdcapi.client.dto.FdcSearchResponse;
import com.fdcapi.exception.FoodNotFoundException;
import com.fdcapi.model.Food;
import com.fdcapi.model.Nutrient;
import com.fdcapi.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FoodServiceImpl implements FoodService {

    private final FdcClient fdcClient;

    public FoodServiceImpl(FdcClient fdcClient) {
        this.fdcClient = fdcClient;
    }

    @Override
    public List<Food> searchFoods(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda no puede estar vacío");
        }

        log.info("Buscando alimentos con query: {}", query);
        FdcSearchResponse response = fdcClient.searchFoods(query.trim());
        
        if (response == null || response.getFoods() == null || response.getFoods().isEmpty()) {
            log.info("No se encontraron resultados para: {}", query);
            return Collections.emptyList();
        }

        List<Food> foods = response.getFoods().stream()
                .map(this::mapToFood)
                .collect(Collectors.toList());
        
        log.info("Se encontraron {} alimentos para: {}", foods.size(), query);
        return foods;
    }

    @Override
    public Food getFoodById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del alimento debe ser un número positivo");
        }

        log.info("Obteniendo detalles del alimento con ID: {}", id);
        FdcFoodItem fdcFoodItem = fdcClient.getFoodDetails(id);
        
        if (fdcFoodItem == null) {
            log.warn("No se encontró alimento con ID: {}", id);
            throw new FoodNotFoundException(id);
        }

        Food food = mapToFood(fdcFoodItem);
        log.info("Detalles obtenidos para: {}", food.getDescription());
        return food;
    }

    private Food mapToFood(FdcFoodItem item) {
        Food food = new Food();
        food.setFdcId(item.getFdcId());
        food.setDescription(item.getDescription());
        food.setBrandOwner(item.getBrandOwner());
        
        log.debug("Mapeando alimento: {} - Nutrientes recibidos: {}", 
                  item.getDescription(), 
                  item.getFoodNutrients() != null ? item.getFoodNutrients().size() : "null");
        
        if (item.getFoodNutrients() != null && !item.getFoodNutrients().isEmpty()) {
            food.setNutrients(item.getFoodNutrients().stream()
                    .map(this::mapToNutrient)
                    .collect(Collectors.toList()));
        } else {
            food.setNutrients(Collections.emptyList());
        }
        
        return food;
    }

    private Nutrient mapToNutrient(FdcNutrient fdcNutrient) {
        Nutrient nutrient = new Nutrient();
        
        // El nutriente tiene un objeto "nutrient" anidado con la información
        if (fdcNutrient.getNutrient() != null) {
            FdcNutrient.NutrientInfo info = fdcNutrient.getNutrient();
            nutrient.setNutrientId(info.getId());
            nutrient.setName(info.getName());
            nutrient.setUnitName(info.getUnitName());
        }
        
        // El valor está en el nivel superior como "amount"
        nutrient.setValue(fdcNutrient.getAmount());
        
        return nutrient;
    }
}
