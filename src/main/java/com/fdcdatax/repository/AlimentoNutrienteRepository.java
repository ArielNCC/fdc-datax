package com.fdcdatax.repository;

import com.fdcdatax.entity.AlimentoNutriente;
import com.fdcdatax.entity.AlimentoNutrienteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlimentoNutrienteRepository extends JpaRepository<AlimentoNutriente, AlimentoNutrienteId> {
}
