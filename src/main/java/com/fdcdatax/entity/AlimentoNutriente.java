package com.fdcdatax.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "alimento_nutriente")
@IdClass(AlimentoNutrienteId.class)
@Data
public class AlimentoNutriente {
    @Id
    @ManyToOne
    @JoinColumn(name = "id_alimento")
    private Alimento alimento;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_nutriente")
    private Nutriente nutriente;

    private Double valor;
}
