// ProveedorController
package com.bike.shop.controller;

import com.bike.shop.dto.request.ProveedorRequestDTO;
import com.bike.shop.dto.response.ProveedorResponseDTO;
import com.bike.shop.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<ProveedorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<ProveedorResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedorService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProveedorResponseDTO> crear(@RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proveedorService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProveedorResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.ok(proveedorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}