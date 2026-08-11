package com.ismael.ticketfu.service;

import com.ismael.ticketfu.entity.Category;
import com.ismael.ticketfu.exception.ResourceNotFoundException;
import com.ismael.ticketfu.repository.CategoryRepository;
import com.ismael.ticketfu.security.JWTAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private JWTAuthenticationFilter jwtAuthenticationFilter; // no se usa, pero el constructor lo requiere

    @InjectMocks
    private CategoryService categoryService;

    @Nested
    @DisplayName("Método create de categoryService")
    class Create {

        @Test
        void shouldCreateCategoryWhenItDoesNotExist() {
            Category category = new Category();
            category.setId(1l); category.setDescription("Todo jose jose "); category.setName("JOse jose");
            when(categoryRepository.existsByName("JOse jose")).thenReturn(false);
            when(categoryRepository.save(category)).thenReturn(category);

            Category guardada = categoryService.create(category);

            assertThat(guardada.getId()).isEqualTo(1L);
            verify(categoryRepository).save(category);
        }

        @Test
        void shouldThrowExceptionWhenCategoryNameAlreadyExists() {
            Category category = new Category();
            category.setId(1l); category.setDescription("Todo jose jose "); category.setName("jose jose");
            when(categoryRepository.existsByName("jose jose")).thenReturn(true);

            assertThatThrownBy(() -> categoryService.create(category))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("La categoria ya exist");
        }
    }

    @Nested
    @DisplayName("Método getAll de Category")
    class GetAll {

        @Test
        void shouldReturnListOfCategories() {
            Category category = new Category();
            category.setId(1l); category.setDescription("Todo jose jose "); category.setName("jose jose");
            when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));

            List<Category> lista = categoryService.getAll();

            assertThat(lista).hasSize(1).containsExactly(category);
        }
    }

    @Nested
    @DisplayName("Método getById de categorias")
    class GetById {

        @Test
        void shouldReturnCategoryWhenItExists() {
            Category category = new Category();
            category.setId(1l); category.setDescription("Todo jose jose "); category.setName("jose jose");
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            Category encontrada = categoryService.getById(1L);

            assertThat(encontrada).isSameAs(category);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
            assertThatThrownBy(() -> categoryService.getById(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("El id está vacío");
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.getById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No existe la categoría con id");
        }
    }

    @Nested
    @DisplayName("Método update")
    class Update {

        @Test
        void shouldUpdateExistingCategory() {
            Category category = new Category();
            category.setId(1l); category.setDescription("Todo jose jose "); category.setName("jose jose");
            Category persistida = new Category();
            persistida.setId(2l); persistida.setDescription("Todo siddhartha"); persistida.setName("Siddhartha");
            when(categoryRepository.findById(2L)).thenReturn(Optional.of(persistida));
            when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

            Category actualizada = categoryService.update(2L, persistida);

            assertThat(actualizada.getName()).isEqualTo("Siddhartha");
            assertThat(actualizada.getDescription()).isEqualTo("Todo siddhartha");
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenParametersAreNull() {
            assertThatThrownBy(() -> categoryService.update(null, new Category()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExistOnUpdate() {
            when(categoryRepository.findById(7L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.update(7L, new Category()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Método delete")
    class Delete {

        @Test
        void shouldDeleteExistingCategory() {
            when(categoryRepository.existsById(3L)).thenReturn(true);
            doNothing().when(categoryRepository).deleteById(3L);

            categoryService.delete(3L);

            verify(categoryRepository).deleteById(3L);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenDeleteIdIsNull() {
            assertThatThrownBy(() -> categoryService.delete(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistingCategory() {
            when(categoryRepository.existsById(10L)).thenReturn(false);

            assertThatThrownBy(() -> categoryService.delete(10L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Método createAll")
    class CreateAll {

        @Test
        void shouldCreateAllCategoriesSuccessfully() {

            Category category1 = new Category();
            category1.setId(1l); category1.setDescription("Todo jose jose "); category1.setName("jose jose");
            Category category2 = new Category();
            category2.setId(2l); category2.setDescription("Todo siddhartha"); category2.setName("Siddhartha");

            List<Category> lista = List.of(category1, category2);
            when(categoryRepository.saveAll(lista))
                    .thenReturn(List.of(category1, category2));

            List<Category> guardadas = categoryService.createAll(lista);

            assertThat(guardadas).hasSize(2);
            verify(categoryRepository).saveAll(lista);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenCategoryListIsEmpty() {
            assertThatThrownBy(() -> categoryService.createAll(Collections.emptyList()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}