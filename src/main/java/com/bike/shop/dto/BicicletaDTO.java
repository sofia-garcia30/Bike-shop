package com.bike.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BicicletaDTO {
    private Integer codigo;
    private String marca;
    private String modelo;
    private String tipo;
    private BigDecimal precioVenta;
    private String descripcion;
    private LocalDateTime fechaRegistro;
}