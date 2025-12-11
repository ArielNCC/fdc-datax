package com.fdcdatax.service;

import com.fdcapi.model.Food;
import com.fdcapi.model.Nutrient;
import com.fdcdatax.entity.Alimento;
import com.fdcdatax.entity.AlimentoNutriente;
import com.fdcdatax.repository.AlimentoNutrienteRepository;
import com.fdcdatax.repository.AlimentoRepository;
import com.fdcdatax.repository.NutrienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataService {

    @Autowired
    private AlimentoRepository alimentoRepository;

    @Autowired
    private NutrienteRepository nutrienteRepository;

    @Autowired
    private AlimentoNutrienteRepository alimentoNutrienteRepository;

    @Autowired
    private com.fdcdatax.repository.ActionLogRepository actionLogRepository;

    public void logAction(String action, String details) {
        com.fdcdatax.entity.ActionLog log = new com.fdcdatax.entity.ActionLog();
        log.setAction(action);
        log.setDetails(details);
        actionLogRepository.save(log);
    }

    public List<com.fdcdatax.entity.ActionLog> getAllLogs() {
        return actionLogRepository.findAllByOrderByTimestampDesc();
    }

    @Transactional
    public void saveFood(Food food) {
        // Save Alimento
        Alimento alimento = new Alimento();
        alimento.setIdAlimento(food.getFdcId());
        alimento.setDescripcion(food.getDescription());
        alimento.setBrandOwner(food.getBrandOwner());
        alimentoRepository.save(alimento);

        // Save Nutrients and Relations
        if (food.getNutrients() != null) {
            for (Nutrient n : food.getNutrients()) {
                // Save Nutriente if not exists or update name
                com.fdcdatax.entity.Nutriente nutrienteEntity = nutrienteRepository.findById(n.getNutrientId())
                        .map(existing -> {
                            // Update name if it was in English and now we have Spanish (or just update always)
                            existing.setNombre(n.getName());
                            existing.setUnidadMedida(n.getUnitName());
                            return nutrienteRepository.save(existing);
                        })
                        .orElseGet(() -> {
                            com.fdcdatax.entity.Nutriente newNutrient = new com.fdcdatax.entity.Nutriente();
                            newNutrient.setIdNutriente(n.getNutrientId());
                            newNutrient.setNombre(n.getName());
                            newNutrient.setUnidadMedida(n.getUnitName());
                            return nutrienteRepository.save(newNutrient);
                        });

                // Save Relation
                AlimentoNutriente relation = new AlimentoNutriente();
                relation.setAlimento(alimento);
                relation.setNutriente(nutrienteEntity);
                relation.setValor(n.getValue());
                alimentoNutrienteRepository.save(relation);
            }
        }
    }

    public List<Food> getAllSavedFoods() {
        return alimentoRepository.findAll().stream()
                .map(this::convertToFood)
                .collect(Collectors.toList());
    }
    
    public Optional<Food> getSavedFoodById(Long id) {
        return alimentoRepository.findById(id).map(this::convertToFood);
    }

    @Transactional
    public void deleteFood(Long id) {
        if (alimentoRepository.existsById(id)) {
            alimentoRepository.deleteById(id);
        }
    }

    private Food convertToFood(Alimento alimento) {
        Food food = new Food();
        food.setFdcId(alimento.getIdAlimento());
        food.setDescription(alimento.getDescripcion());
        food.setBrandOwner(alimento.getBrandOwner());
        
        List<Nutrient> nutrients = new ArrayList<>();
        if (alimento.getNutrientes() != null) {
            for (AlimentoNutriente an : alimento.getNutrientes()) {
                Nutrient n = new Nutrient();
                n.setNutrientId(an.getNutriente().getIdNutriente());
                n.setName(an.getNutriente().getNombre());
                n.setUnitName(an.getNutriente().getUnidadMedida());
                n.setValue(an.getValor());
                nutrients.add(n);
            }
        }
        food.setNutrients(nutrients);
        return food;
    }
}
