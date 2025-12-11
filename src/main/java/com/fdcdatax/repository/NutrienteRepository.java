package com.fdcdatax.repository;

import com.fdcdatax.entity.Nutriente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrienteRepository extends JpaRepository<Nutriente, Long> {
}
