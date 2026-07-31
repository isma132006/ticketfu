package com.ismael.ticketfu.repository;



import com.ismael.ticketfu.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder a la entidad {@link Users}.
 *
 * Extiende {@link JpaRepository}, donde:
 * <ul>
 *   <li>{@code Users}: representa la entidad que será administrada.</li>
 *   <li>{@code Long}: representa el tipo de dato de la llave primaria (ID) de la entidad.</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico del usuario.
     * @return un {@code Optional} que contiene el usuario si existe,
     *         o un {@code Optional.empty()} si no se encontró.
     */
    Optional<Users> findByEmail(String email);

    /**
     * Verifica si un email existe al registrarse
     * @param email
     * @return
     */
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}