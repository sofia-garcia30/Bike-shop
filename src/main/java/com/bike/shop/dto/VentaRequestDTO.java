package com.bike.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// Lo que el FRONTEND ENVÍA para crear una venta
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {
    private String documentoCliente;
    private String formaPago;
    private List<DetalleVentaRequestDTO> detalles;
}
