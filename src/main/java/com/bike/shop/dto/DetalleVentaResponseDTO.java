package com.bike.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaResponseDTO {
    private Integer id;
    private Integer codigoBicicleta;
    private String marcaModelo;        // campo extra: útil para mostrar en frontend
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
