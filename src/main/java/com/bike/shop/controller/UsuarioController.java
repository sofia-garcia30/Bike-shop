package com.bike.shop.controller;

import com.bike.shop.dto.request.UsuarioRequestDTO;
import com.bike.shop.dto.response.UsuarioResponseDTO;
import com.bike.shop.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // GET /api/usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // POST /api/usuarios
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.crear(dto));
    }

    // PUT /api/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    // PATCH /api/usuarios/{id}/desactivar
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioResponseDTO> desactivar(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.desactivar(id));
    }

    // PATCH /api/usuarios/{id}/activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<UsuarioResponseDTO> activar(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.activar(id));
    }
}
