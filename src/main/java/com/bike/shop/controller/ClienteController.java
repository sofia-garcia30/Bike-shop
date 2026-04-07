package com.bike.shop.controller;

import com.bike.shop.dto.ClienteDTO;
import com.bike.shop.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "https://bike-shop-frontend-production.up.railway.app")
public class ClienteController {

    private final ClienteService clienteService;

    // GET /clientes
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    // GET /clientes/{documento}
    @GetMapping("/{documento}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<ClienteDTO> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(clienteService.buscarPorDocumento(documento));
    }

    // GET /clientes/buscar?termino=xxx
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<List<ClienteDTO>> buscar(@RequestParam String termino) {
        return ResponseEntity.ok(clienteService.buscarPorTermino(termino));
    }

    // POST /clientes
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<ClienteDTO> crear(@RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(dto));
    }

    // PUT /clientes/{documento}
    @PutMapping("/{documento}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable String documento,
                                                 @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizar(documento, dto));
    }
}
