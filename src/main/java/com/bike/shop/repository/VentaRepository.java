package com.bike.shop.repository;

import com.bike.shop.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByClienteDocumento(String documento);
    List<Venta> findByEstado(String estado);
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT v FROM Venta v WHERE v.total >= :montoMinimo ORDER BY v.fecha DESC")
    List<Venta> findByTotalMayorIgual(@Param("montoMinimo") Double montoMinimo);
}