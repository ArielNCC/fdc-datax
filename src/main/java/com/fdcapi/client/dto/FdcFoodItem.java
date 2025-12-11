package com.fdcapi.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FdcFoodItem {
    private Long fdcId;
    private String description;
    private String brandOwner;
    private List<FdcNutrient> foodNutrients;
}
