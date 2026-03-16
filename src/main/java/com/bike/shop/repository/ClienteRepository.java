package com.bike.shop.repository;

import com.bike.shop.entity.Bicicleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Integer> {

    List<Bicicleta> findByMarca(String marca);

    List<Bicicleta> findByTipo(String tipo);

}