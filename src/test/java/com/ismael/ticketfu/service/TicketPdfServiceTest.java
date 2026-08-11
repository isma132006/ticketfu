package com.ismael.ticketfu.service; import com.ismael.ticketfu.entity.Event; import com.ismael.ticketfu.entity.Ticket; import com.ismael.ticketfu.entity.TicketStatus; import com.ismael.ticketfu.entity.Users; import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.InjectMocks; import org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension; import java.awt.image.BufferedImage; import java.io.ByteArrayOutputStream; import java.math.BigDecimal; import java.time.LocalDateTime; import javax.imageio.ImageIO; import static org.assertj.core.api.Assertions.assertThat; import static org.mockito.ArgumentMatchers.anyInt; import static org.mockito.ArgumentMatchers.anyString; import static org.mockito.ArgumentMatchers.eq; import static org.mockito.Mockito.times; import static org.mockito.Mockito.verify; import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketPdfServiceTest {

    @Mock
    private TicketQrService ticketQrService;

    @InjectMocks
    private TicketPdfService pdfService;

    private Ticket dummyTicket() {

        Users user = new Users();
        user.setId(1L);
        user.setEmail("ismael@example.com");

        Event event = new Event();
        event.setId(10L);
        event.setName("Concierto de Jazz");
        event.setLocalDateTime(LocalDateTime.now().plusDays(5));
        event.setVenue("Teatro Central");
        event.setPrice(BigDecimal.valueOf(30));

        Ticket ticket = new Ticket();
        ticket.setId(100L);
        ticket.setUser(user);
        ticket.setEvent(event);
        ticket.setTicketStatus(TicketStatus.AVAILABLE);
        ticket.setQrCode("ticket-100-qr");

        return ticket;
    }

    @Test
    void shouldGenerateTicketPdfSuccessfully() throws Exception {

        // Arrange
        BufferedImage image = new BufferedImage(
                200,
                200,
                BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ImageIO.write(image, "png", outputStream);

        byte[] fakeQr = outputStream.toByteArray();

        when(ticketQrService.generateQRCodeImage(
                anyString(),
                anyInt(),
                anyInt()
        )).thenReturn(fakeQr);

        Ticket ticket = dummyTicket();

        // Act
        byte[] pdf = pdfService.generateTicketPdf(ticket);

        // Assert
        assertThat(pdf)
                .isNotNull()
                .isNotEmpty();

        verify(ticketQrService, times(1))
                .generateQRCodeImage(
                        eq(ticket.getQrCode()),
                        anyInt(),
                        anyInt()
                );
    }
}


