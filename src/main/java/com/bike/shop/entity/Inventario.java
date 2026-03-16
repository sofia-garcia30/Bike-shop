package com.bike.shop.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Data
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "codigo_bicicleta", nullable = false)
    private Bicicleta bicicleta;

    private Integer cantidad;

    private Integer stockMinimo;

    private Integer stockMaximo;

    private String ubicacion;

    private LocalDateTime fechaActualizacion;
}