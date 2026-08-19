package com.ismael.ticketfu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ismael.ticketfu.dto.request.RegisterRequestDto;
import com.ismael.ticketfu.dto.response.UserResponse;
import com.ismael.ticketfu.security.JWTAuthenticationFilter;
import com.ismael.ticketfu.security.JWTService;
import com.ismael.ticketfu.security.SecurityConfig;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import com.ismael.ticketfu.entity.Role;
import com.ismael.ticketfu.entity.Users;
import com.ismael.ticketfu.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // O @MockBean en versiones anteriores de Spring Boot
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 1. Apunta al Controlador que estás probando
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JWTAuthenticationFilter.class, SecurityConfig.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

 @Autowired
 private MockMvc mockMvc; // Cliente HTTP simulado

 // Simula JWTService para que el filtro JWTAuthenticationFilter pueda construirse
 @MockitoBean
 private JWTService jwtService;

 @MockitoBean //
 private UserService userService;

 /**
  * NO tiene service
  * no tiene DTO
  * no tien db
  * no tiene parametros
  * solo responder Hola y ver que sea lo mismo
  */
 @Test
 @DisplayName("SI alguien hace GET/hola, responde Hola")
 void shouldReturnHolaWhenGetHola() throws Exception {

  // --- WHEN & THEN (Cuando & Entonces) ---
  mockMvc.perform(get("/hola")) // Cambia la ruta según tu Controller real
          // Assertions HTTP
          .andExpect(status().isOk())
          .andExpect(content().string("Hola"));
 }

 @Test
 @DisplayName("Debería describir el login.")
 void shouldReturnTokenWhenLogin() throws Exception {
  // --- GIVEN (Dado) ---
  //crear un usuario
  Users usuario = Users.builder()
          .id(1L)
          .firstName("Ismael")
          .lastName("soto")
          .email("ismael@example.com")
          .password("123456")
          .role(Role.ROLE_CUSTOMER)
          .isEnabled(true)
          .build();

  //hacer un json con mi usuario pues esperamos eso
  ObjectMapper usuarioJson = new ObjectMapper();
  try{
   //hacemos el mappeo
   String json = usuarioJson.writeValueAsString(usuario);

   //verificar mock de user
   Mockito.when(userService.verificar(Mockito.any(Users.class)))
           .thenReturn("jwt-token");

   // --- WHEN & THEN (Cuando & Entonces) ---
   mockMvc.perform(post("/login")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(json))
           .andExpect(status().isOk())
           .andExpect(content().string("jwt-token"));

   Mockito.verify(userService).verificar(Mockito.any(Users.class));

  }catch (Exception e){
   e.printStackTrace();
  }


 }
 @Test
 @DisplayName("Devuleve un userResponse cuando se registra")
 void shouldReturnTokenWHenRegisterIsCorrect() throws Exception {
  RegisterRequestDto request = RegisterRequestDto.builder()
          .firstName("Ismael")
          .lastName("Soto")
          .email("ismael@example.com")
          .password("123456")
          .phoneNumber("+1234567890")
          .build();
  UserResponse response = UserResponse.builder()
          .id(1L)
          .firstName("Ismael")
          .lastName("García")
          .email("ismael@example.com")
          .phoneNumber("+1234567890")
          .build();

  //creamos el json de usuario
  ObjectMapper objectMapper = new ObjectMapper();
  String json = objectMapper.writeValueAsString(request);

  //verificar mock de user
  Mockito.when(userService.register(Mockito.any(RegisterRequestDto.class)))
          .thenReturn(response);

  // --- WHEN & THEN (Cuando & Entonces) ---
  mockMvc.perform(post("/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(json))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.firstName").value("Ismael"))
          .andExpect(jsonPath("$.lastName").value("García"))
          .andExpect(jsonPath("$.email").value("ismael@example.com"))
          .andExpect(jsonPath("$.phoneNumber").value("+1234567890"));

  Mockito.verify(userService)
          .register(Mockito.any(RegisterRequestDto.class));

 }
}