package com.bike.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Integer id;
    private String nombre;
    private String email;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
