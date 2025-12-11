package com.fdcdatax.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "alimento")
@Data
public class Alimento {
    @Id
    @Column(name = "id_alimento")
    private Long idAlimento; // fdcId

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "brand_owner")
    private String brandOwner;

    @OneToMany(mappedBy = "alimento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AlimentoNutriente> nutrientes;
}
