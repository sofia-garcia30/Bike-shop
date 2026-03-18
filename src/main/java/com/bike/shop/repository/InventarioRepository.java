package com.bike.shop.repository;

import com.bike.shop.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByBicicletaCodigo(Integer bicicletaCodigo);
    List<Inventario> findByUbicacionIgnoreCase(String ubicacion);

    @Query("SELECT i FROM Inventario i WHERE i.cantidad <= i.stockMinimo")
    List<Inventario> findStockBajo();

    @Query("SELECT i FROM Inventario i WHERE i.cantidad = 0")
    List<Inventario> findSinStock();

    @Query("SELECT i FROM Inventario i WHERE i.cantidad >= :minimo ORDER BY i.cantidad ASC")
    List<Inventario> findByStockMayorIgual(@Param("minimo") Integer minimo);
}