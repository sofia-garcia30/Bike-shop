package com.bike.shop.service;

import com.bike.shop.dto.request.LoginRequestDTO;
import com.bike.shop.dto.response.LoginResponseDTO;
import com.bike.shop.entity.Usuario;
import com.bike.shop.exception.ValidacionException;
import com.bike.shop.repository.UsuarioRepository;
import com.bike.shop.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO request) {
        // 1. Validar que vengan los datos
        if (request.getEmail() == null || request.getEmail().isBlank())
            throw new ValidacionException("El email es obligatorio");
        if (request.getPassword() == null || request.getPassword().isBlank())
            throw new ValidacionException("La contraseña es obligatoria");

        // 2. Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidacionException(
                        "Credenciales incorrectas"));

        // 3. Verificar que esté activo
        if (!usuario.getActivo())
            throw new ValidacionException("Usuario inactivo — contacte al administrador");

        // 4. Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword()))
            throw new ValidacionException("Credenciales incorrectas");

        // 5. Generar token JWT
        String token = jwtUtil.generarToken(usuario);

        return new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}
