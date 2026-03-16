package com.bike.shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "documento_cliente")
    private Cliente cliente;

    private LocalDateTime fecha;

    private BigDecimal total;

    @Column(name = "forma_pago")
    private String formaPago;

    private String estado;
}