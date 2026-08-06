package com.ismael.ticketfu.service;

import com.ismael.ticketfu.dto.request.PurchaseTicketRequest;
import com.ismael.ticketfu.dto.response.TicketResponse;
import com.ismael.ticketfu.entity.*;
import com.ismael.ticketfu.exception.ResourceNotFoundException;
import com.ismael.ticketfu.repository.EventRepository;
import com.ismael.ticketfu.repository.TicketRepository;
import com.ismael.ticketfu.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


//ver los casos que hace un metodo en el service
//EL test debe ser como lo que hace

// extendwith(...) hace el trabajo de crear e inyectar estos objetos mocks en injectsmocks
@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    //Crear objetos falsos con @Mock, que en este caso son
    //los atributos de ticketService
    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    //con Test al ejecutar Junit busca metodos con la notacion y los ejecuta unoXuno

    // ===============================================
    // Tests para PurchaseTicket()
    // ==============================================
    @Test
    void shouldPurchaseTicketSuccessfully() {

    //CREAR UN ESCENARIO FALSO PARA HACER TESTS
        //crear Event con boletos disponibles
        Event event = new Event();
        event.setId(1L);
        event.setName("Concierto");
        //No es necesario event.setDescription("Gran evento en vivo");
        // no es necsario event.setLocalDateTime(LocalDateTime.now().plusDays(7));
        // NO es necesario event.setVenue("Estadio GNP");
        event.setCapacity(100);
        event.setAvailableTickets(50); // Boletos disponibles
        event.setPrice(new BigDecimal("500"));

        // Crear User
        Users user = new Users();
        user.setId(1L);
        // no es necesario user.setFirstName("Ismael");
        // no es necesario user.setLastName("Soto");
        user.setEmail("ismael@Test.com");
        // no es necesario user.setPhoneNumber("123456789");
        // no es necesariouser.setPassword("1234");
        user.setRole(Role.ROLE_CUSTOMER);
        user.setIsEnabled(true);

        //Crear PurchaseTicketRequest
        PurchaseTicketRequest request = new PurchaseTicketRequest();
        request.setEventId(event.getId()); // el id del evento
        request.setQuantity(3); //los boletos que se desean comorar

    //AAA
    //ARRANCE -> Preparar el escenario
        // Configuramos el comportamiento del Mock.
        // Cuando se llame a este metodo, devolverá el objeto indicado.
        when(eventRepository.findByIdWithLock(request.getEventId()))
                .thenReturn(Optional.of(event));

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

    //Act -> ejecutar el metodo que deseamos probar

        TicketResponse resultado = ticketService.purchaseTicket(request, user.getEmail());


    //Assert-> comprobar que ocurrio lo esperado.

        assertNotNull(resultado);
        assertEquals("Concierto", resultado.getEventName() );

        //aqui es el email
        assertEquals(user.getEmail(), resultado.getUserName());

        assertNotNull(resultado.getQrCode());
        assertEquals(TicketStatus.PURCHASED, resultado.getTicketStatus());
        assertEquals(49, event.getAvailableTickets());
        //verify
//        verify(ticketRepository).save(any());

        //este test si pasara
        verify(ticketRepository, times(1))
                .save(any(Ticket.class));

    }
    // ===============================================
    // Tests para Create()
    // ==============================================
    @Test
    void shouldCreateTicketSuccessfully() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setQrCode("QR-12345");

        when(ticketRepository.existsByQrCode(ticket.getQrCode()))
                .thenReturn(false);

        when(ticketRepository.save(ticket))
                .thenReturn(ticket);
        // Act
        Ticket resultado = ticketService.create(ticket);
        // Assert
        assertNotNull(resultado);
        assertEquals(ticket, resultado);

        verify(ticketRepository, times(1))
                .existsByQrCode(ticket.getQrCode());

        verify(ticketRepository, times(1))
                .save(ticket);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTicketIsNull() {
        //Arrange
        Ticket ticket = null;

        //act

        //guarda la ex
        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.create(ticket)
        );

        assertEquals("El ticket no puede ser nulo", exception.getMessage());

        verify(ticketRepository, never()).save(any());
        verify(ticketRepository, never()).existsByQrCode(any());
    }


    @Test
    void shouldThrowIllegalArgumentExceptionWhenQrCodeAlreadyExists() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setQrCode("QR-12345");

        when(ticketRepository.existsByQrCode(ticket.getQrCode()))
                .thenReturn(true);
        // Act
        //guarda la ex
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> ticketService.create(ticket)
                );

        assertEquals("El código QR ya existe", exception.getMessage());

        verify(ticketRepository, never()).save(any());

        verify(ticketRepository, times(1))
                .existsByQrCode(ticket.getQrCode());

    }
    // ===============================================
    // Tests para GetById()
    // ==============================================
    @Test
    void ShouldGetByIdSuccessfully(){
        //arrange
        Ticket ticket = new Ticket();
        ticket.setQrCode("QR-12345");
        ticket.setId(1L);

        when(ticketRepository.findById(ticket.getId()))
                .thenReturn(Optional.of(ticket));

        //act
        Ticket resultado = ticketService.getById(ticket.getId());

        //assert
        assertNotNull(resultado);
        assertEquals(ticket.getQrCode(), resultado.getQrCode() );


        //este test si pasara
        verify(ticketRepository, times(1)).findById(anyLong());
    }
    @Test
    void shouldThrowIllegalArgumentExceptionWhenIdIsNull(){
        //Arrange
        Ticket ticket = new Ticket();
        ticket.setId(null);
        //act

        //guarda la ex
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> ticketService.getById(ticket.getId())
                );

        assertEquals("El id está vacío", exception.getMessage());

        verify(ticketRepository, never()).findById(anyLong());

    }
    @Test
    void shouldThrowResourceNotFoundExceptionWhenTicketDoesNotExist(){
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setQrCode("QR-12345");
        ticket.setId(1L);
        // Act
        when(ticketRepository.findById(ticket.getId()))
                .thenReturn(Optional.empty());
        //guarda la ex
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> ticketService.getById(ticket.getId())
                );

        assertEquals("No existe el ticket con id: " + ticket.getId(), exception.getMessage());

        verify(ticketRepository, times(1))
                .findById(ticket.getId());
    }
    // ===============================================
    // Tests para Update()
    // ==============================================

    void shouldUpdateTicketSuccessfully(){
        //arrange
        Ticket ticket = new Ticket();
        ticket.setQrCode("QR-12345");
        ticket.setId(1L);
        ticket.setTicketStatus(TicketStatus.CANCELLED);

        //CREAR UN ESCENARIO FALSO PARA HACER TESTS
        Event event = new Event();
        event.setId(1L);
        event.setName("Concierto");

        // Crear User
        Users user = new Users();
        user.setId(1L);
        user.setEmail("ismael@Test.com");


        when(ticketRepository.findById(ticket.getId()))
                .thenReturn(Optional.of(ticket));

        Ticket nuevo = new Ticket();
        nuevo.setUser(user);
        nuevo.setEvent(event);
        nuevo.setTicketStatus(TicketStatus.PURCHASED);

        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //act
        Ticket resultado = ticketService.update(ticket.getId(),nuevo );

        //assert
        assertNotNull(resultado);

        assertEquals(user, resultado.getUser());
        assertEquals(event, resultado.getEvent());
        assertEquals(TicketStatus.PURCHASED, resultado.getTicketStatus());

        verify(ticketRepository, times(1))
                .findById(ticket.getId());

        verify(ticketRepository, times(1)).save(ticket);

    }

    //update con id o tickte nulls
    //update con el id no found


    // ===============================================
    // Tests para delete()
    // ==============================================
    @Test
    void shouldDeleteTicketSuccessfully(){
        //arrange
        Ticket ticket = new Ticket();
        ticket.setQrCode("QR-12345");
        ticket.setId(1L);

        when(ticketRepository.existsById(ticket.getId()))
                .thenReturn(true);

        //act
        ticketService.delete(ticket.getId());

        //assert
        //este test si pasara
        verify(ticketRepository, times(1)).deleteById(ticket.getId());
    }
    @Test
    void shouldThrowIllegalArgumentExceptionWhenDeleteIdIsNull(){
        //Arrange
        Ticket ticket = new Ticket();
        ticket.setId(null);
        //act

        //guarda la ex
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> ticketService.delete(ticket.getId())
                );

        assertEquals("El id esta vacia", exception.getMessage());

        verify(ticketRepository, never()).deleteById(anyLong());
    }
    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistingTicket(){
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setQrCode("QR-12345");
        ticket.setId(1L);
        // Act
        when(ticketRepository.existsById(ticket.getId()))
                .thenReturn(false);
        //guarda la ex
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> ticketService.delete(ticket.getId())
                );

        assertEquals("No existe el ticket con ese ID", exception.getMessage());

        verify(ticketRepository, times(1))
                .existsById(ticket.getId());
        verify(ticketRepository, never())
                .deleteById(anyLong());
    }

}
