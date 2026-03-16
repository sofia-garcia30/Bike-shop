package com.bike.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {
    private Integer id;
    private Integer codigoBicicleta;
    private String marcaModelo;        // campo extra: útil para mostrar en frontend
    private Integer cantidad;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private String ubicacion;
    private LocalDateTime fechaActualizacion;
}
