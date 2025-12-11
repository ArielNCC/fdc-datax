package com.fdcapi.model;

import lombok.Data;

@Data
public class Nutrient {
    private Long nutrientId;
    private String name;
    private Double value;
    private String unitName;
}
