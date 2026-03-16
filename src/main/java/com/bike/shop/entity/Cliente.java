package com.bike.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    private String documento;

    private String nombre;

    private String telefono;

    private String email;

    private String direccion;
}