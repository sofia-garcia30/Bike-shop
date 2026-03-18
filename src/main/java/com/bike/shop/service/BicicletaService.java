package com.bike.shop.service;

import com.bike.shop.dto.BicicletaDTO;
import com.bike.shop.dto.InventarioDTO;
import com.bike.shop.entity.Bicicleta;
import com.bike.shop.entity.Inventario;
import com.bike.shop.repository.BicicletaRepository;
import com.bike.shop.repository.InventarioRepository;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BicicletaService {

    private final BicicletaRepository bicicletaRepository;
    private final InventarioRepository inventarioRepository;

    // ─── LISTAR TODAS ───────────────────────────────────────────
    public List<BicicletaDTO> listarTodas() {
        return bicicletaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── BUSCAR POR CÓDIGO ───────────────────────────────────────
    public BicicletaDTO buscarPorCodigo(Integer codigo) {
        Bicicleta bicicleta = bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una bicicleta con código " + codigo));
        return toDTO(bicicleta);
    }

    // ─── BUSCAR POR MARCA ────────────────────────────────────────
    public List<BicicletaDTO> buscarPorMarca(String marca) {
        return bicicletaRepository.findByMarcaIgnoreCase(marca)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── BUSCAR POR TIPO ─────────────────────────────────────────
    public List<BicicletaDTO> buscarPorTipo(String tipo) {
        return bicicletaRepository.findByTipoIgnoreCase(tipo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── REGISTRAR ───────────────────────────────────────────────
    @Transactional
    public BicicletaDTO registrar(BicicletaDTO dto) {
        validarBicicleta(dto);

        Bicicleta bicicleta = toEntity(dto);
        Bicicleta guardada = bicicletaRepository.save(bicicleta);

        // Crear inventario inicial con cantidad 0
        Inventario inventario = new Inventario();
        inventario.setBicicleta(guardada);
        inventario.setCantidad(0);
        inventario.setStockMinimo(5);
        inventario.setStockMaximo(50);
        inventario.setUbicacion("Sin asignar");
        inventarioRepository.save(inventario);

        return toDTO(guardada);
    }

    // ─── ACTUALIZAR ──────────────────────────────────────────────
    @Transactional
    public BicicletaDTO actualizar(Integer codigo, BicicletaDTO dto) {
        Bicicleta existente = bicicletaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una bicicleta con código " + codigo));

        validarBicicleta(dto);

        existente.setMarca(dto.getMarca());
        existente.setModelo(dto.getModelo());
        existente.setTipo(dto.getTipo());
        existente.setPrecioVenta(dto.getPrecioVenta());
        existente.setDescripcion(dto.getDescripcion());

        return toDTO(bicicletaRepository.save(existente));
    }

    // ─── ELIMINAR ────────────────────────────────────────────────
    @Transactional
    public void eliminar(Integer codigo) {
        if (!bicicletaRepository.existsById(codigo)) {
            throw new RecursoNoEncontradoException(
                    "No existe una bicicleta con código " + codigo);
        }
        bicicletaRepository.deleteById(codigo);
    }

    // ─── ACTUALIZAR INVENTARIO ───────────────────────────────────
    @Transactional
    public InventarioDTO actualizarInventario(Integer CodigoBicicleta, Integer nuevaCantidad) {
        if (nuevaCantidad < 0) {
            throw new ValidacionException("La cantidad en inventario no puede ser negativa");
        }

        Inventario inventario = inventarioRepository.findByBicicletaCodigo(CodigoBicicleta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe inventario para la bicicleta con código " + CodigoBicicleta));

        inventario.setCantidad(nuevaCantidad);
        Inventario actualizado = inventarioRepository.save(inventario);

        return toInventarioDTO(actualizado);
    }

    // ─── VALIDACIONES DE NEGOCIO ─────────────────────────────────
    private void validarBicicleta(BicicletaDTO dto) {
        if (dto.getMarca() == null || dto.getMarca().isBlank()) {
            throw new ValidacionException("La marca es obligatoria");
        }
        if (dto.getModelo() == null || dto.getModelo().isBlank()) {
            throw new ValidacionException("El modelo es obligatorio");
        }
        if (dto.getPrecioVenta() == null) {
            throw new ValidacionException("El precio de venta es obligatorio");
        }
        if (dto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException("El precio de venta debe ser mayor a 0");
        }
    }

    // ─── CONVERSORES ─────────────────────────────────────────────
    private BicicletaDTO toDTO(Bicicleta b) {
        BicicletaDTO dto = new BicicletaDTO();
        dto.setCodigo(b.getCodigo());
        dto.setMarca(b.getMarca());
        dto.setModelo(b.getModelo());
        dto.setTipo(b.getTipo());
        dto.setPrecioVenta(b.getPrecioVenta());
        dto.setDescripcion(b.getDescripcion());
        dto.setFechaRegistro(b.getFechaRegistro());
        return dto;
    }

    private Bicicleta toEntity(BicicletaDTO dto) {
        Bicicleta b = new Bicicleta();
        b.setMarca(dto.getMarca());
        b.setModelo(dto.getModelo());
        b.setTipo(dto.getTipo());
        b.setPrecioVenta(dto.getPrecioVenta());
        b.setDescripcion(dto.getDescripcion());
        return b;
    }

    private InventarioDTO toInventarioDTO(Inventario i) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(i.getId());
        dto.setCodigoBicicleta(i.getBicicleta().getCodigo());
        dto.setMarcaModelo(i.getBicicleta().getMarca() + " " + i.getBicicleta().getModelo());
        dto.setCantidad(i.getCantidad());
        dto.setStockMinimo(i.getStockMinimo());
        dto.setStockMaximo(i.getStockMaximo());
        dto.setUbicacion(i.getUbicacion());
        dto.setFechaActualizacion(i.getFechaActualizacion());
        return dto;
    }
}
