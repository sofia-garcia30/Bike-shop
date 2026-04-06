package com.bike.shop.service;

import com.bike.shop.dto.request.UsuarioRequestDTO;
import com.bike.shop.dto.response.UsuarioResponseDTO;
import com.bike.shop.entity.Usuario;
import com.bike.shop.exception.DuplicateResourceException;
import com.bike.shop.exception.RecursoNoEncontradoException;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── LISTAR TODOS ────────────────────────────────────────────
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ─── BUSCAR POR ID ───────────────────────────────────────────
    public UsuarioResponseDTO buscarPorId(Integer id) {
        return toDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe usuario con id " + id)));
    }

    // ─── CREAR ───────────────────────────────────────────────────
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        validar(dto, true);

        if (usuarioRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException(
                    "Ya existe un usuario con email: " + dto.getEmail());

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol() != null ? dto.getRol().toUpperCase() : "EMPLEADO");
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        return toDTO(usuarioRepository.save(usuario));
    }

    // ─── ACTUALIZAR ──────────────────────────────────────────────
    public UsuarioResponseDTO actualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe usuario con id " + id));

        if (dto.getNombre() != null && !dto.getNombre().isBlank())
            usuario.setNombre(dto.getNombre());

        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail()))
                throw new DuplicateResourceException(
                        "Ya existe un usuario con email: " + dto.getEmail());
            usuario.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getRol() != null && !dto.getRol().isBlank())
            usuario.setRol(dto.getRol().toUpperCase());

        return toDTO(usuarioRepository.save(usuario));
    }

    // ─── DESACTIVAR ──────────────────────────────────────────────
    public UsuarioResponseDTO desactivar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe usuario con id " + id));

        if ("ADMIN".equals(usuario.getRol())) {
            long adminsActivos = usuarioRepository.countByRolAndActivoTrue("ADMIN");
            if (adminsActivos <= 1) {
                throw new ValidacionException("No se puede desactivar al único administrador del sistema");
            }
        }
        usuario.setActivo(false);
        return toDTO(usuarioRepository.save(usuario));
    }

    // ─── ACTIVAR ─────────────────────────────────────────────────
    public UsuarioResponseDTO activar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe usuario con id " + id));
        usuario.setActivo(true);
        return toDTO(usuarioRepository.save(usuario));
    }

    // ─── VALIDACIONES ────────────────────────────────────────────
    private void validar(UsuarioRequestDTO dto, boolean esNuevo) {
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new ValidacionException("El nombre es obligatorio");
        if (dto.getEmail() == null || dto.getEmail().isBlank())
            throw new ValidacionException("El email es obligatorio");
        if (esNuevo && (dto.getPassword() == null || dto.getPassword().isBlank()))
            throw new ValidacionException("La contraseña es obligatoria");
        if (dto.getRol() != null
                && !dto.getRol().equalsIgnoreCase("ADMIN")
                && !dto.getRol().equalsIgnoreCase("EMPLEADO"))
            throw new ValidacionException("El rol debe ser ADMIN o EMPLEADO");
    }

    // ─── CONVERSOR ───────────────────────────────────────────────
    private UsuarioResponseDTO toDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getRol(),
                u.getActivo(),
                u.getFechaCreacion()
        );
    }
}