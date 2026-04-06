package com.bike.shop.repository;

import com.bike.shop.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRol(String rol);

    List<Usuario> findByActivoTrue();

    long countByRolAndActivoTrue(String rol);
}
