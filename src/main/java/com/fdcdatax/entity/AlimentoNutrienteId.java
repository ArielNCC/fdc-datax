package com.fdcdatax.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlimentoNutrienteId implements Serializable {
    private Long alimento;
    private Long nutriente;
}
