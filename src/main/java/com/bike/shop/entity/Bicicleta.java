package com.bike.shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bicicleta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bicicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    private String tipo;

    @Column(name = "precio_venta")
    private BigDecimal precioVenta;

    private String descripcion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}