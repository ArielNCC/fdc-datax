package com.fdcapi.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FdcNutrient {
    private NutrientInfo nutrient;  // Objeto anidado con información del nutriente
    private Double amount;           // Cantidad del nutriente (valor)
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NutrientInfo {
        private Long id;             // ID del nutriente
        private String number;       // Número del nutriente (como string)
        private String name;         // Nombre del nutriente
        private String unitName;     // Unidad de medida
    }
}
