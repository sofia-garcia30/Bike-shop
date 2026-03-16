package com.bike.shop.repository;

import com.bike.shop.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByBicicletaCodigo(Integer codigoBicicleta);

}