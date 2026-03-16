package com.bike.shop.repository;

import com.bike.shop.entity.Bicicleta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Integer> {
}
