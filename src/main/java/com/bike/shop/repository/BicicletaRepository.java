package com.bike.shop.repository;

import com.bike.shop.entity.Bicicleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Integer> {
    List<Bicicleta> findByMarcaIgnoreCase(String marca);
    List<Bicicleta> findByTipoIgnoreCase(String tipo);
    List<Bicicleta> findByPrecioVentaBetween(Double precioMin, Double precioMax);

    @Query("SELECT b FROM Bicicleta b WHERE LOWER(b.marca) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(b.modelo) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(b.descripcion) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Bicicleta> buscarPorTermino(@Param("termino") String termino);
}
