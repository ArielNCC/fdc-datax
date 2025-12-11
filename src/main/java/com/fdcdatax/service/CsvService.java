package com.fdcdatax.service;

import com.fdcapi.model.Food;
import com.fdcapi.model.Nutrient;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class CsvService {

    public ByteArrayInputStream generateCsv(Food food) {
        StringBuilder csv = new StringBuilder();
        csv.append("FDC ID,Description,Brand Owner\n");
        csv.append(escape(food.getFdcId())).append(",");
        csv.append(escape(food.getDescription())).append(",");
        csv.append(escape(food.getBrandOwner())).append("\n\n");

        csv.append("Nutrient ID,Name,Value,Unit\n");
        if (food.getNutrients() != null) {
            for (Nutrient n : food.getNutrients()) {
                csv.append(escape(n.getNutrientId())).append(",");
                csv.append(escape(n.getName())).append(",");
                csv.append(escape(n.getValue())).append(",");
                csv.append(escape(n.getUnitName())).append("\n");
            }
        }

        return new ByteArrayInputStream(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String escape(Object data) {
        if (data == null) {
            return "";
        }
        String str = data.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }
}
