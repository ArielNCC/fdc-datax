package com.fdcdatax.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "nutriente")
@Data
public class Nutriente {
    @Id
    @Column(name = "id_nutriente")
    private Long idNutriente; // nutrientId

    @Column(nullable = false)
    private String nombre;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    private String descripcion;
}
