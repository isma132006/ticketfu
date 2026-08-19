package com.ismael.ticketfu.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ismael.ticketfu.dto.request.RegisterRequestDto;
import com.ismael.ticketfu.dto.response.UserResponse;
import com.ismael.ticketfu.entity.Category;
import com.ismael.ticketfu.security.JWTAuthenticationFilter;
import com.ismael.ticketfu.security.JWTService;
import com.ismael.ticketfu.security.SecurityConfig;
import com.ismael.ticketfu.service.CategoryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // O @MockBean en versiones anteriores de Spring Boot
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 1. Apunta al Controlador que estás probando
@WebMvcTest(
        controllers = CategoryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JWTAuthenticationFilter.class, SecurityConfig.class}
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    // Simula JWTService para que el filtro JWTAuthenticationFilter pueda construirse
    @MockitoBean
    private JWTService jwtService;

    @MockitoBean //
    private CategoryService categoryService;

    @Test
    @DisplayName("Obtiene todas la categorias")
    void shouldReturnAllCategoriesWhenFound() throws Exception {

        Category categoriaTest = new Category(
                1L,
                "Conciertos",
                "Boletos para los mejores eventos de música en vivo"
        );
        System.out.println(categoriaTest.toString());

        List<Category> lista = new ArrayList<>();
        lista.add(categoriaTest);
        //verificar mock de user


        //verificar mock de user
        Mockito.when(categoryService.getAll())
                .thenReturn(lista);

        // --- WHEN & THEN (Cuando & Entonces) ---
        System.out.println(categoriaTest.getId());
        System.out.println(categoriaTest.getName());
        System.out.println(categoriaTest.getDescription());
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Boletos para los mejores eventos de música en vivo"))
                .andExpect(jsonPath("$[0].name").value("Conciertos"));

        Mockito.verify(categoryService).getAll();// verifica que el metodo GetAll() del service se invoco

    };
    @Test
    @DisplayName("Obtiene la categoria por un id ")
    void shouldReturnCategoryWhenFound() throws Exception {
        Category categoriaTest = new Category(
                1L,
                "Conciertos",
                "Boletos para los mejores eventos de música en vivo"
        );


        //verificar mock de category
        Mockito.when(categoryService.getById(categoriaTest.getId()))
                .thenReturn(categoriaTest);

        // --- WHEN & THEN (Cuando & Entonces) ---
        mockMvc.perform(get("/categories/{id}", categoriaTest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Boletos para los mejores eventos de música en vivo"))
                .andExpect(jsonPath("$.name").value("Conciertos"));

        Mockito.verify(categoryService).getById(1L);// verifica que el metodo getbyid() del service se invoco
    }

    @Test
    @DisplayName("Crea una categoria ")
    void shouldCreateCategorySuccessfully() throws Exception {
        Category categoriaTest = new Category(
                1L,
                "Conciertos",
                "Boletos para los mejores eventos de música en vivo"
        );

        //creamos el json de una categoria
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(categoriaTest);


        //verificar mock de category
        Mockito.when(categoryService.create(Mockito.any(Category.class)))
                .thenReturn(categoriaTest);

        // --- WHEN & THEN (Cuando & Entonces) ---
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Boletos para los mejores eventos de música en vivo"))
                .andExpect(jsonPath("$.name").value("Conciertos"));

        Mockito.verify(categoryService).create(Mockito.any(Category.class));// verifica que el metodo create()

    }
    @Test
    @DisplayName("ELimina una categoria")
    void shoulDeeleteCategorySuccessfully() throws Exception {
        Category categoriaTest = new Category(
                1L,
                "Conciertos",
                "Boletos para los mejores eventos de música en vivo"
        );

        Mockito.when(categoryService.getById(categoriaTest.getId()))
                        .thenReturn(categoriaTest);
        //verificar mock de category
        Mockito.doNothing()
                        .when(categoryService)
                                .delete(categoriaTest.getId());

        // --- WHEN & THEN (Cuando & Entonces) ---
        mockMvc.perform(delete("/categories/{id}", categoriaTest.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Conciertos"))
                .andExpect(jsonPath("$.description")
                        .value("Boletos para los mejores eventos de música en vivo"));

        Mockito.verify(categoryService).getById(categoriaTest.getId());// verifica que el metodo getByid()
        Mockito.verify(categoryService).delete(categoriaTest.getId());// verifica que el metodo delete()

    }

}
