package com.bike.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Lo que el BACKEND DEVUELVE después de crear/consultar una venta
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {
    private Integer id;
    private String documentoCliente;
    private String nombreCliente;      // útil para mostrar en frontend
    private LocalDateTime fecha;
    private BigDecimal total;
    private String formaPago;
    private String estado;
    private List<DetalleVentaResponseDTO> detalles;
}
