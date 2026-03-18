package com.bike.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertasStockDTO {
    private Integer id;
    private Integer codigoBicicleta;
    private String mensaje;
    private LocalDateTime fecha;
    private Boolean leido;
}
