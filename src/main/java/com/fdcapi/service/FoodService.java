package com.fdcapi.service;

import com.fdcapi.model.Food;
import java.util.List;

public interface FoodService {
    List<Food> searchFoods(String query);
    Food getFoodById(Long id);
}
