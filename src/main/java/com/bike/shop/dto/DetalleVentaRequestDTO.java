package com.bike.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Un ítem dentro del request de venta
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaRequestDTO {
    private Integer codigoBicicleta;
    private Integer cantidad;
    // precio_unitario NO se envía: el trigger lo toma de bicicleta.precio_venta
}
