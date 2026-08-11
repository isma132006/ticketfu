package com.ismael.ticketfu.service;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TicketQrServiceTest {

    private final TicketQrService qrService = new TicketQrService();

    @Test
    @DisplayName("Genera una imagen QR válida para texto no vacío")
    void shouldGenerateQRCodeImageSuccessfully() throws WriterException, IOException {
        byte[] qrBytes = qrService.generateQRCodeImage("prueba-qr", 200, 200);
        assertThat(qrBytes).isNotNull().isNotEmpty();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTextIsNull() {

        assertThatThrownBy(() ->
                qrService.generateQRCodeImage(null, 200, 200)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El texto no puede ser nulo o vacío");
    }
}