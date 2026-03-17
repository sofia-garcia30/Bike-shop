package com.bike.shop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BicicletaDTO {
    private Long id;
    private String codigo;
    private String marca;
    private String modelo;
    private String tipo;
    private Double precioVenta;
    private String descripcion;
}