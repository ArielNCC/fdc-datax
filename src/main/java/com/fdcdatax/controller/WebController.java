package com.fdcdatax.controller;

import com.fdcapi.model.Food;
import com.fdcapi.service.FoodService;
import com.fdcdatax.service.DataService;
import com.fdcdatax.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class WebController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private DataService dataService;

    @Autowired
    private CsvService csvService;

    @Autowired
    private com.fdcdatax.service.TranslationService translationService;

    @GetMapping
    public String index() {
        return "inicio/index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query,
                         @RequestParam(required = false) Long id,
                         Model model) {
        dataService.logAction("SEARCH", "Query: " + query + ", ID: " + id);
        if (id != null) {
            return "redirect:/details/" + id;
        } else if (query != null && !query.trim().isEmpty()) {
            // Translate query to English
            String englishQuery = translationService.translateSearchTerm(query);
            
            List<Food> foods = foodService.searchFoods(englishQuery);
            if (foods == null || foods.isEmpty()) {
                model.addAttribute("message", "No se encontraron resultados para: " + query + " (" + englishQuery + ")");
            } else {
                // Translate results for display (optional for list view, but good for consistency)
                foods.forEach(translationService::translateFood);
                model.addAttribute("foods", foods);
            }
            return "resultados/results-list";
        }
        return "inicio/index";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model) {
        try {
            Food food = foodService.getFoodById(id);
            // Translate nutrients to Spanish
            translationService.translateFood(food);
            
            model.addAttribute("food", food);
            model.addAttribute("isSaved", false);
            return "resultados/details";
        } catch (Exception e) {
            model.addAttribute("error", "Alimento no encontrado con ID: " + id);
            return "error";
        }
    }

    @PostMapping("/save/{id}")
    public String saveFood(@PathVariable Long id) {
        try {
            Food food = foodService.getFoodById(id);
            // Guardamos en inglés (sin traducir) para mantener la consistencia en la BD
            
            dataService.saveFood(food);
            dataService.logAction("SAVE", "Saved Food ID: " + id);
            return "redirect:/my-db";
        } catch (Exception e) {
             return "redirect:/details/" + id + "?error=save_failed";
        }
    }

    @GetMapping("/my-db")
    public String myDb(Model model) {
        model.addAttribute("foods", dataService.getAllSavedFoods());
        return "my-bd/my-db";
    }
    
    @GetMapping("/my-db/{id}")
    public String savedFoodDetails(@PathVariable Long id, Model model) {
        Optional<Food> food = dataService.getSavedFoodById(id);
        if (food.isPresent()) {
            Food foodItem = food.get();
            // Traducir para la vista
            translationService.translateFood(foodItem);
            
            model.addAttribute("food", foodItem);
            model.addAttribute("isSaved", true);
            return "resultados/details";
        }
        return "redirect:/my-db";
    }

    @PostMapping("/my-db/delete/{id}")
    public String deleteFood(@PathVariable Long id) {
        dataService.deleteFood(id);
        dataService.logAction("DELETE", "Deleted Food ID: " + id);
        return "redirect:/my-db";
    }

    @GetMapping("/download/csv/{id}")
    public ResponseEntity<InputStreamResource> downloadCsv(@PathVariable Long id) {
        try {
            // Try to find in DB first, then API? Or just API?
            // Assuming we want to download what we see.
            // If we are in "details" view, we might have come from API or DB.
            // For simplicity, let's try API first, then DB if fails (or vice versa depending on context).
            // But wait, the ID is unique.
            Food food = null;
            try {
                food = foodService.getFoodById(id);
            } catch (Exception e) {
                food = dataService.getSavedFoodById(id).orElse(null);
            }

            if (food != null) {
                ByteArrayInputStream stream = csvService.generateCsv(food);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=food_" + id + ".csv");
                dataService.logAction("DOWNLOAD_CSV", "Downloaded CSV for ID: " + id);
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.parseMediaType("application/csv"))
                        .body(new InputStreamResource(stream));
            }
        } catch (Exception e) {
            // Log error
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("logs", dataService.getAllLogs());
        return "configuracion/history";
    }

    @GetMapping("/settings")
    public String settings() {
        return "configuracion/settings";
    }
    
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}

