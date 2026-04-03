// VentaService
package com.bike.shop.service;

import com.bike.shop.dto.response.DetalleVentaResponseDTO;
import com.bike.shop.dto.request.VentaRequestDTO;
import com.bike.shop.dto.response.VentaResponseDTO;
import com.bike.shop.entity.Cliente;
import com.bike.shop.entity.DetalleVenta;
import com.bike.shop.entity.Venta;
import com.bike.shop.entity.Usuario;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.BicicletaRepository;
import com.bike.shop.repository.ClienteRepository;
import com.bike.shop.repository.DetalleVentaRepository;
import com.bike.shop.repository.VentaRepository;
import com.bike.shop.entity.Bicicleta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final BicicletaRepository bicicletaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // GET todas las ventas
    public List<VentaResponseDTO> listarTodas() {
        return ventaRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // GET ventas del usuario logueado (para empleado)
    public List<VentaResponseDTO> listarMisVentas() {
        Usuario usuarioLogueado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return ventaRepository.findByUsuarioId(
                        usuarioLogueado.getId().longValue())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<VentaResponseDTO> listarPorUsuario(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // GET mis ventas por fecha (para empleado)
    public List<VentaResponseDTO> listarMisVentasPorFecha(
            LocalDateTime inicio, LocalDateTime fin) {
        Usuario usuarioLogueado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return ventaRepository.findByUsuarioIdAndFechaBetween(
                        usuarioLogueado.getId().longValue(), inicio, fin)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // GET venta por id
    public VentaResponseDTO buscarPorId(Integer id) {
        return toResponseDTO(ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe venta con id " + id)));
    }

    // GET ventas por cliente
    public List<VentaResponseDTO> buscarPorCliente(String documento) {
        return ventaRepository.findByClienteDocumento(documento)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // POST crear venta
    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO dto) {
        // ✅ Obtener usuario logueado del token
        Usuario usuarioLogueado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getDocumentoCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe cliente con documento " + dto.getDocumentoCliente()));

        // Validar detalles
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty())
            throw new ValidacionException("La venta debe tener al menos un detalle");

        // Crear venta
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuarioLogueado); // ✅ agregar usuario
        venta.setFecha(LocalDateTime.now());
        venta.setFormaPago(dto.getFormaPago());
        venta.setEstado("completada");
        venta.setTotal(BigDecimal.ZERO);
        Venta guardada = ventaRepository.save(venta);
        ventaRepository.flush();

        // Crear detalles
        dto.getDetalles().forEach(d -> {
            Bicicleta bicicleta = bicicletaRepository.findById(d.getCodigoBicicleta())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe bicicleta con código " + d.getCodigoBicicleta()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(guardada);
            detalle.setBicicleta(bicicleta);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(BigDecimal.ZERO);
            detalle.setSubtotal(BigDecimal.ZERO);
            detalle = detalleVentaRepository.save(detalle);
            detalleVentaRepository.flush();
        });

        entityManager.flush();
        entityManager.clear();

        // Recargar venta con valores actualizados por triggers
        Venta ventaActualizada = ventaRepository.findById(guardada.getId()).get();
        return toResponseDTO(ventaActualizada);


    }

    // PATCH cancelar venta
    @Transactional
    public VentaResponseDTO cancelar(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe venta con id " + id));

        if (venta.getEstado().equals("devuelta"))
            throw new ValidacionException("La venta ya fue cancelada");

        venta.setEstado("devuelta");
        return toResponseDTO(ventaRepository.save(venta));
    }

    private VentaResponseDTO toResponseDTO(Venta v) {
        List<DetalleVentaResponseDTO> detalles = detalleVentaRepository
                .findByVentaId(v.getId())
                .stream()
                .map(d -> new DetalleVentaResponseDTO(
                        d.getId(),
                        d.getBicicleta().getCodigo(),
                        d.getBicicleta().getMarca() + " " + d.getBicicleta().getModelo(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new VentaResponseDTO(
                v.getId(),
                v.getCliente().getDocumento(),
                v.getCliente().getNombre(),
                v.getUsuario().getId(),
                v.getUsuario().getNombre(),
                v.getFecha(),
                v.getTotal(),
                v.getFormaPago(),
                v.getEstado(),
                detalles
        );
    }

    public List<VentaResponseDTO> buscarPorFecha(
            LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaBetween(inicio, fin)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
