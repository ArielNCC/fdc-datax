package com.fdcdatax.service;

import com.fdcapi.model.Food;
import com.fdcapi.model.Nutrient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
public class TranslationService {

    // Usamos TreeMap con String.CASE_INSENSITIVE_ORDER para ignorar mayúsculas/minúsculas
    private static final Map<String, String> NUTRIENT_TRANSLATIONS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        // Nutrientes (Inglés -> Español)
        NUTRIENT_TRANSLATIONS.put("Energy", "Energía");
        NUTRIENT_TRANSLATIONS.put("Total lipid (fat)", "Grasa Total");
        NUTRIENT_TRANSLATIONS.put("Fatty acids, total saturated", "Grasa Saturada");
        NUTRIENT_TRANSLATIONS.put("Fatty acids, total trans", "Grasa Trans");
        NUTRIENT_TRANSLATIONS.put("Cholesterol", "Colesterol");
        NUTRIENT_TRANSLATIONS.put("Sodium, Na", "Sodio");
        NUTRIENT_TRANSLATIONS.put("Carbohydrate, by difference", "Carbohidratos Totales");
        NUTRIENT_TRANSLATIONS.put("Fiber, total dietary", "Fibra Dietética");
        NUTRIENT_TRANSLATIONS.put("Sugars, total including NLEA", "Azúcares Totales");
        NUTRIENT_TRANSLATIONS.put("Added sugars", "Azúcares Añadidos");
        NUTRIENT_TRANSLATIONS.put("Protein", "Proteína");
        NUTRIENT_TRANSLATIONS.put("Vitamin D (D2 + D3)", "Vitamina D");
        NUTRIENT_TRANSLATIONS.put("Calcium, Ca", "Calcio");
        NUTRIENT_TRANSLATIONS.put("Iron, Fe", "Hierro");
        NUTRIENT_TRANSLATIONS.put("Potassium, K", "Potasio");
        NUTRIENT_TRANSLATIONS.put("Vitamin A, RAE", "Vitamina A");
        NUTRIENT_TRANSLATIONS.put("Vitamin C, total ascorbic acid", "Vitamina C");
        NUTRIENT_TRANSLATIONS.put("Water", "Agua");
        NUTRIENT_TRANSLATIONS.put("Ash", "Cenizas");
        NUTRIENT_TRANSLATIONS.put("Caffeine", "Cafeína");
        NUTRIENT_TRANSLATIONS.put("Alcohol, ethyl", "Alcohol");
        NUTRIENT_TRANSLATIONS.put("Magnesium, Mg", "Magnesio");
        NUTRIENT_TRANSLATIONS.put("Phosphorus, P", "Fósforo");
        NUTRIENT_TRANSLATIONS.put("Theobromine", "Teobromina");
        NUTRIENT_TRANSLATIONS.put("Total Sugars", "Azúcares Totales");

    }

    public String translateSearchTerm(String term) {
        return term;
    }

    public void translateFood(Food food) {
        if (food == null || food.getNutrients() == null) return;

        for (Nutrient nutrient : food.getNutrients()) {
            String englishName = nutrient.getName();
            if (englishName == null) continue;

            // Limpiar espacios extra
            String cleanName = englishName.trim();
            
            String spanishName = NUTRIENT_TRANSLATIONS.get(cleanName);
            
            if (spanishName != null) {
                nutrient.setName(spanishName);
            } else {
                // Fallback para coincidencias parciales
                if (cleanName.contains("Vitamin")) {
                    nutrient.setName(cleanName.replace("Vitamin", "Vitamina"));
                } else if (cleanName.contains("Fatty acids")) {
                    nutrient.setName(cleanName.replace("Fatty acids", "Ácidos grasos"));
                }
            }
        }
    }
}
