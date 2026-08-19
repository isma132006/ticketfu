package com.ismael.ticketfu.service;

import com.ismael.ticketfu.dto.request.RegisterRequestDto;
import com.ismael.ticketfu.dto.response.UserResponse;
import com.ismael.ticketfu.entity.Role;
import com.ismael.ticketfu.entity.Users;
import com.ismael.ticketfu.exception.ResourceNotFoundException;
import com.ismael.ticketfu.repository.UserRepository;
import com.ismael.ticketfu.security.JWTService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JWTService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("Registra usuario y devuelve UserResponse")
        void registraUsuarioCorrectamente() {
            RegisterRequestDto req = new RegisterRequestDto();
            req.setFirstName("Ana");
            req.setLastName("López");
            req.setEmail("ana@example.com");
            req.setPhoneNumber("5551234");
            req.setPassword("pwd123");

            when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
            when(passwordEncoder.encode("pwd123")).thenReturn("encoded-pwd");
            when(userRepository.save(any(Users.class))).thenAnswer(i -> {
                Users u = i.getArgument(0);
                u.setId(1L);
                return u;
            });

            UserResponse resp = userService.register(req);

            assertThat(resp.getId()).isEqualTo(1L);
            assertThat(resp.getEmail()).isEqualTo("ana@example.com");
            verify(userRepository).save(argThat(u ->
                    u.getFirstName().equals("Ana") &&
                            u.getRole() == Role.ROLE_CUSTOMER &&
                            u.getIsEnabled()));
        }

        @Test
        @DisplayName("Lanza excepción si el email ya está registrado")
        void lanzaExceptionCuandoEmailExiste() {
            RegisterRequestDto req = new RegisterRequestDto();
            req.setEmail("existente@example.com");
            when(userRepository.existsByEmail("existente@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.register(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ya existe");
        }
    }

    @Nested
    @DisplayName("verificar (login)")
    class Verificar {

        @Test
        @DisplayName("Retorna token JWT cuando credenciales son válidas")
        void retornaJwtAlAutenticarCorrectamente() {
            Users user = new Users();
            user.setEmail("ismael");
            user.setPassword("pwd");

            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(auth);
            when(jwtService.generarToken("ismael")).thenReturn("jwt-token");

            String token = userService.verificar(user);
            assertThat(token).isEqualTo("jwt-token");
        }

        @Test
        @DisplayName("Lanza excepción cuando la autenticación falla")
        void lanzaExceptionSiCredencialesInvalidas() {
            Users user = new Users();
            user.setEmail("bad");
            user.setPassword("wrong");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Error en la autenticación"));

            assertThatThrownBy(() -> userService.verificar(user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error en la autenticación");
        }
    }

    @Nested
    @DisplayName("Operaciones CRUD")
    class Crud {

        @Test
        void getAll_devuelveListaDeUsuarios() {
            Users u = new Users();
            u.setId(1L);
            when(userRepository.findAll()).thenReturn(Collections.singletonList(u));

            var lista = userService.getAll();

            assertThat(lista).hasSize(1).containsExactly(u);
        }

        @Test
        void getById_devuelveUsuarioCuandoExiste() {
            Users u = new Users();
            u.setId(2L);
            when(userRepository.findById(2L)).thenReturn(Optional.of(u));

            Users encontrado = userService.getById(2L);
            assertThat(encontrado).isSameAs(u);
        }

        @Test
        void getById_lanzaExceptionSiIdNulo() {
            assertThatThrownBy(() -> userService.getById(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void getById_lanzaResourceNotFoundSiNoExiste() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getById(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void update_modificaCamposCorrectamente() {
            Users origen = new Users();
            origen.setFirstName("Carlos");
            origen.setPassword("newPass");

            Users persistido = new Users();
            persistido.setId(5L);
            persistido.setFirstName("Viejo");

            when(userRepository.findById(5L)).thenReturn(Optional.of(persistido));
            when(passwordEncoder.encode("newPass")).thenReturn("encNewPass");
            when(userRepository.save(any(Users.class))).thenAnswer(i -> i.getArgument(0));

            Users actualizado = userService.update(5L, origen);

            assertThat(actualizado.getFirstName()).isEqualTo("Carlos");
            assertThat(actualizado.getPassword()).isEqualTo("encNewPass");
        }

        @Test
        void delete_eliminaUsuarioExistente() {
            when(userRepository.existsById(3L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(3L);

            userService.delete(3L);
            verify(userRepository).deleteById(3L);
        }

        @Test
        void delete_lanzaExceptionSiIdNulo() {
            assertThatThrownBy(() -> userService.delete(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void delete_lanzaResourceNotFoundSiNoExiste() {
            when(userRepository.existsById(10L)).thenReturn(false);
            assertThatThrownBy(() -> userService.delete(10L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void createAll_guardaListaDeUsuarios() {
            Users u1 = new Users();
            u1.setPassword("a");
            Users u2 = new Users();
            u2.setPassword("b");
            var lista = java.util.List.of(u1, u2);

            when(userRepository.saveAll(lista)).thenReturn(lista);
            when(passwordEncoder.encode(anyString())).thenReturn("enc");

            var guardados = userService.createAll(lista);
            assertThat(guardados).hasSize(2);
            verify(userRepository).saveAll(lista);
        }
    }
}