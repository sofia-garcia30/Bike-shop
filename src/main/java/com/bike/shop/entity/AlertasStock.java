package com.bike.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertasStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_bicicleta", nullable = false)
    private Bicicleta bicicleta;

    @Column(nullable = false, length = 255)
    private String mensaje;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Boolean leido = false;
}
