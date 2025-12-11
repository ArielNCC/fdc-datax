package com.fdcapi.service.mappers;

import com.fdcapi.client.dto.FdcFoodItem;
import com.fdcapi.client.dto.FdcNutrient;
import com.fdcapi.model.Food;
import com.fdcapi.model.Nutrient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FoodMapper {

    private FoodMapper() {
        // Utility class
    }

    public static Food toFood(FdcFoodItem fdcItem) {
        if (fdcItem == null) {
            return null;
        }

        Food food = new Food();
        food.setFdcId(fdcItem.getFdcId());
        food.setDescription(fdcItem.getDescription());
        food.setBrandOwner(fdcItem.getBrandOwner());
        
        if (fdcItem.getFoodNutrients() != null) {
            food.setNutrients(fdcItem.getFoodNutrients().stream()
                    .map(FoodMapper::toNutrient)
                    .collect(Collectors.toList()));
        } else {
            food.setNutrients(Collections.emptyList());
        }
        
        return food;
    }

    public static Nutrient toNutrient(FdcNutrient fdcNutrient) {
        if (fdcNutrient == null || fdcNutrient.getNutrient() == null) {
            return null;
        }

        Nutrient nutrient = new Nutrient();
        nutrient.setNutrientId(fdcNutrient.getNutrient().getId());
        nutrient.setName(fdcNutrient.getNutrient().getName());
        nutrient.setValue(fdcNutrient.getAmount());
        nutrient.setUnitName(fdcNutrient.getNutrient().getUnitName());
        
        return nutrient;
    }

    public static List<Food> toFoodList(List<FdcFoodItem> fdcItems) {
        if (fdcItems == null) {
            return Collections.emptyList();
        }
        
        return fdcItems.stream()
                .map(FoodMapper::toFood)
                .collect(Collectors.toList());
    }
}
