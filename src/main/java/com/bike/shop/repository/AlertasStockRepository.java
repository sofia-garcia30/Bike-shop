package com.bike.shop.repository;

import com.bike.shop.entity.AlertasStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertasStockRepository extends JpaRepository<AlertasStock, Integer> {

    // Todas las alertas de una bicicleta específica
    List<AlertasStock> findByBicicleta_Codigo(Integer codigoBicicleta);

    // Solo las alertas no leídas (para el dashboard)
    List<AlertasStock> findByLeidoFalse();

    // Contar alertas activas (útil para el badge del dashboard)
    long countByLeidoFalse();
}
