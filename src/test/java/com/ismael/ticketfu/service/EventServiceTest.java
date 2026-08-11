package com.ismael.ticketfu.service;

import com.ismael.ticketfu.entity.Category;
import com.ismael.ticketfu.entity.Event;
import com.ismael.ticketfu.exception.ResourceNotFoundException;
import com.ismael.ticketfu.repository.CategoryRepository;
import com.ismael.ticketfu.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private EventService eventService;

    private Category dummyCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Conciertos");
        category.setDescription("Música en vivo");

        return category;
    }

    private Event dummyEvent() {
        Event ev = new Event();
        ev.setId(1L);
        ev.setName("Rock Fest");
        ev.setDescription("Festival de rock");
        ev.setLocalDateTime(LocalDateTime.now().plusDays(10));
        ev.setVenue("Auditorio Central");
        ev.setCapacity(200);
        ev.setAvailableTickets(200);
        ev.setPrice(new BigDecimal("50.0"));
        ev.setCategory(dummyCategory());
        return ev;
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Nested
    @DisplayName("Método create")
    class Create {

        @Test
        void shouldCreateEventSuccessfully() {

            // Arrange
            Event event = dummyEvent();

            when(eventRepository.existsByName(event.getName()))
                    .thenReturn(false);

            when(categoryRepository.findById(event.getCategory().getId()))
                    .thenReturn(Optional.of(dummyCategory()));

            when(eventRepository.save(any(Event.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Event result = eventService.create(event);

            // Assert
            assertNotNull(result);
            assertEquals(event.getName(), result.getName());
            assertEquals(event.getCategory(), result.getCategory());

            verify(eventRepository, times(1))
                    .existsByName(event.getName());

            verify(categoryRepository, times(1))
                    .findById(event.getCategory().getId());

            verify(eventRepository, times(1))
                    .save(event);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenEventAlreadyExists() {

            // Arrange
            Event event = dummyEvent();

            when(eventRepository.existsByName(event.getName()))
                    .thenReturn(true);

            // Act
            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> eventService.create(event)
                    );

            // Assert
            assertEquals("El evento ya existe", exception.getMessage());

            verify(eventRepository, times(1))
                    .existsByName(event.getName());

            verify(eventRepository, never())
                    .save(any(Event.class));
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenCategoryIsNull() {

            // Arrange
            Event event = dummyEvent();
            event.setCategory(null);

            // Act
            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> eventService.create(event)
                    );

            // Assert
            assertEquals(
                    "El evento debe incluir un objeto category con su id",
                    exception.getMessage()
            );

            verify(eventRepository, never())
                    .save(any(Event.class));
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {

            // Arrange
            Event event = dummyEvent();

            Category category = new Category();
            category.setId(99L);

            event.setCategory(category);

            when(eventRepository.existsByName(event.getName()))
                    .thenReturn(false);

            when(categoryRepository.findById(99L))
                    .thenReturn(Optional.empty());

            // Act
            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> eventService.create(event)
                    );

            // Assert
            assertTrue(
                    exception.getMessage()
                            .contains("No existe la categoría con id")
            );

            verify(categoryRepository, times(1))
                    .findById(99L);

            verify(eventRepository, never())
                    .save(any(Event.class));
        }
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Nested
    @DisplayName("Método getAll")
    class GetAll {

        @Test
        void shouldReturnListOfEvents() {

            // Arrange
            Event event = dummyEvent();

            when(eventRepository.findAll())
                    .thenReturn(Collections.singletonList(event));

            // Act
            List<Event> result = eventService.getAll();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(event, result.get(0));

            verify(eventRepository, times(1))
                    .findAll();
        }
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Nested
    @DisplayName("Método getById")
    class GetById {

        @Test
        void shouldReturnEventWhenEventExists() {

            // Arrange
            Event event = dummyEvent();

            when(eventRepository.findById(event.getId()))
                    .thenReturn(Optional.of(event));

            // Act
            Event result = eventService.getById(event.getId());

            // Assert
            assertNotNull(result);
            assertEquals(event, result);

            verify(eventRepository, times(1))
                    .findById(event.getId());
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {

            // Act
            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> eventService.getById(null)
                    );

            // Assert
            assertEquals("El id está vacío", exception.getMessage());

            verify(eventRepository, never())
                    .findById(anyLong());
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenEventDoesNotExist() {

            // Arrange
            Long id = 99L;

            when(eventRepository.findById(id))
                    .thenReturn(Optional.empty());

            // Act
            ResourceNotFoundException exception =
                    assertThrows(
                            ResourceNotFoundException.class,
                            () -> eventService.getById(id)
                    );

            // Assert
            assertTrue(
                    exception.getMessage()
                            .contains("No existe el evento con id")
            );

            verify(eventRepository, times(1))
                    .findById(id);
        }
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Nested
    @DisplayName("Método update")
    class Update {

        @Test
        void shouldUpdateEventSuccessfully() {

            // Arrange
            Event existingEvent = dummyEvent();
            existingEvent.setId(10L);
            existingEvent.setName("Evento anterior");
            existingEvent.setVenue("Lugar anterior");

            Event newData = dummyEvent();
            newData.setName("Nuevo nombre");
            newData.setVenue("Nuevo lugar");

            when(eventRepository.findById(10L))
                    .thenReturn(Optional.of(existingEvent));

            when(eventRepository.save(any(Event.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Event result =
                    eventService.update(10L, newData);

            // Assert
            assertNotNull(result);
            assertEquals("Nuevo nombre", result.getName());
            assertEquals("Nuevo lugar", result.getVenue());

            verify(eventRepository, times(1))
                    .findById(10L);

            verify(eventRepository, times(1))
                    .save(existingEvent);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenParametersAreNull() {

            // Act
            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> eventService.update(null, new Event())
                    );

            // Assert
            assertNotNull(exception);

            verify(eventRepository, never())
                    .findById(anyLong());

            verify(eventRepository, never())
                    .save(any(Event.class));
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenEventDoesNotExist() {

            // Arrange
            Long id = 7L;

            when(eventRepository.findById(id))
                    .thenReturn(Optional.empty());

            // Act
            ResourceNotFoundException exception =
                    assertThrows(
                            ResourceNotFoundException.class,
                            () -> eventService.update(id, new Event())
                    );

            // Assert
            assertNotNull(exception);

            verify(eventRepository, times(1))
                    .findById(id);

            verify(eventRepository, never())
                    .save(any(Event.class));
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Nested
    @DisplayName("Método delete")
    class Delete {

        @Test
        void shouldDeleteEventSuccessfully() {

            // Arrange
            Long id = 3L;

            when(eventRepository.existsById(id))
                    .thenReturn(true);

            // Act
            eventService.delete(id);

            // Assert
            verify(eventRepository, times(1))
                    .existsById(id);

            verify(eventRepository, times(1))
                    .deleteById(id);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {

            // Act
            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> eventService.delete(null)
                    );

            // Assert
            assertNotNull(exception);

            verify(eventRepository, never())
                    .existsById(anyLong());

            verify(eventRepository, never())
                    .deleteById(anyLong());
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenEventDoesNotExist() {

            // Arrange
            Long id = 10L;

            when(eventRepository.existsById(id))
                    .thenReturn(false);

            // Act
            ResourceNotFoundException exception =
                    assertThrows(
                            ResourceNotFoundException.class,
                            () -> eventService.delete(id)
                    );

            // Assert
            assertNotNull(exception);

            verify(eventRepository, times(1))
                    .existsById(id);

            verify(eventRepository, never())
                    .deleteById(anyLong());
        }
    }

    // =========================================================
    // CREATE ALL
    // =========================================================

    @Nested
    @DisplayName("Método createAll")
    class CreateAll {

        @Test
        void shouldCreateAllEventsSuccessfully() {

            // Arrange
            Event event1 = dummyEvent();
            event1.setName("Evento 1");

            Event event2 = dummyEvent();
            event2.setName("Evento 2");

            List<Event> events =
                    List.of(event1, event2);

            when(eventRepository.saveAll(events))
                    .thenReturn(events);

            // Act
            List<Event> result =
                    eventService.createAll(events);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());

            verify(eventRepository, times(1))
                    .saveAll(events);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenEventListIsEmpty() {

            // Arrange
            List<Event> events = Collections.emptyList();

            // Act
            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> eventService.createAll(events)
                    );

            // Assert
            assertNotNull(exception);

            verify(eventRepository, never())
                    .saveAll(anyList());
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenEventListIsNull() {

            // Arrange
            List<Event> events = null;

            // Act
            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> eventService.createAll(events)
                    );

            // Assert
            assertNotNull(exception);

            verify(eventRepository, never())
                    .saveAll(anyList());
        }
    }
}