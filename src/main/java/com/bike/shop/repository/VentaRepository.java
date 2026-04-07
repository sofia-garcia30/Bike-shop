// VentaRepository — ya lo tienes, solo verifica
package com.bike.shop.repository;

import com.bike.shop.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.math.BigDecimal;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByClienteDocumento(String documento);
    List<Venta> findByEstado(String estado);
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    // ⭐ NUEVOS MÉTODOS PARA REPORTES POR USUARIO
    List<Venta> findByUsuarioId(Long usuarioId);
    List<Venta> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    // ⭐ NUEVO: Ventas por usuario y rango de fechas
    List<Venta> findByUsuarioIdAndFechaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    // ⭐ NUEVO: Contar ventas por usuario
    long countByUsuarioId(Long usuarioId);

    // ⭐ NUEVO: Total de ventas por usuario
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.usuario.id = :usuarioId")
    BigDecimal sumTotalByUsuarioId(Long usuarioId);

    @Query("SELECT dv.bicicleta.codigo, " +
            "dv.bicicleta.marca, " +
            "dv.bicicleta.modelo, " +
            "SUM(dv.cantidad) as totalVendido " +
            "FROM DetalleVenta dv " +
            "GROUP BY dv.bicicleta.codigo, dv.bicicleta.marca, dv.bicicleta.modelo " +
            "ORDER BY totalVendido DESC")
    List<Object[]> findTopBicicletas();
}