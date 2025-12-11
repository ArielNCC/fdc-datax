package com.fdcapi.model;

import java.util.List;
import lombok.Data;

@Data
public class Food {
    private Long fdcId;
    private String description;
    private String brandOwner;
    private List<Nutrient> nutrients;
}
