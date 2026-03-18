package com.bike.shop.controller;

import com.bike.shop.dto.BicicletaDTO;
import com.bike.shop.dto.InventarioDTO;
import com.bike.shop.service.BicicletaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bicicletas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BicicletaController {

    private final BicicletaService bicicletaService;

    // GET /api/bicicletas
    @GetMapping
    public ResponseEntity<List<BicicletaDTO>> listarTodas() {
        return ResponseEntity.ok(bicicletaService.listarTodas());
    }

    // GET /api/bicicletas/{codigo}
    @GetMapping("/{codigo}")
    public ResponseEntity<BicicletaDTO> buscarPorCodigo(@PathVariable Integer codigo) {
        return ResponseEntity.ok(bicicletaService.buscarPorCodigo(codigo));
    }

    // GET /api/bicicletas/buscar?marca=GW
    @GetMapping("/buscar")
    public ResponseEntity<List<BicicletaDTO>> buscar(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String tipo) {

        if (marca != null && !marca.isBlank()) {
            return ResponseEntity.ok(bicicletaService.buscarPorMarca(marca));
        }
        if (tipo != null && !tipo.isBlank()) {
            return ResponseEntity.ok(bicicletaService.buscarPorTipo(tipo));
        }
        return ResponseEntity.ok(bicicletaService.listarTodas());
    }

    // POST /api/bicicletas
    @PostMapping
    public ResponseEntity<BicicletaDTO> registrar(@RequestBody BicicletaDTO dto) {
        BicicletaDTO creada = bicicletaService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // PUT /api/bicicletas/{codigo}
    @PutMapping("/{codigo}")
    public ResponseEntity<BicicletaDTO> actualizar(
            @PathVariable Integer codigo,
            @RequestBody BicicletaDTO dto) {
        return ResponseEntity.ok(bicicletaService.actualizar(codigo, dto));
    }

    // DELETE /api/bicicletas/{codigo}
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer codigo) {
        bicicletaService.eliminar(codigo);
        return ResponseEntity.noContent().build();
    }

    // PUT /api/bicicletas/{codigo}/inventario?cantidad=10
    @PutMapping("/{codigo}/inventario")
    public ResponseEntity<InventarioDTO> actualizarInventario(
            @PathVariable Integer codigo,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(bicicletaService.actualizarInventario(codigo, cantidad));
    }
}