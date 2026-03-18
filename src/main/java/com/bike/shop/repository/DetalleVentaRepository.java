package com.bike.shop.repository;

import com.bike.shop.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVentaId(Long ventaId);
    List<DetalleVenta> findByBicicletaCodigo(Long bicicletaCodigo);

    @Query("SELECT SUM(d.cantidad) FROM DetalleVenta d WHERE d.bicicleta.codigo = :bicicletaCodigo")
    Integer totalUnidadesVendidasPorBicicleta(@Param("bicicletaCodigo") Long bicicletaCodigo);
}