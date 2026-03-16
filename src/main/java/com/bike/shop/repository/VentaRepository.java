package com.bike.shop.repository;

import com.bike.shop.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByDocumentoCliente(String documentoCliente);

}