// PedidoController
package com.bike.shop.controller;

import com.bike.shop.dto.request.PedidoRequestDTO;
import com.bike.shop.dto.response.PedidoResponseDTO;
import com.bike.shop.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "https://bike-shop-frontend-production.up.railway.app")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> crear(@RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crear(dto));
    }

    @PatchMapping("/{id}/recibido")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<PedidoResponseDTO> marcarRecibido(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.marcarRecibido(id));
    }

    @GetMapping("/proveedor/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorProveedor(
            @PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.buscarPorProveedor(id));
    }
}