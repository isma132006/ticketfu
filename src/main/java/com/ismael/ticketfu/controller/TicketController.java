package com.ismael.ticketfu.controller;

import com.google.zxing.WriterException;
import com.ismael.ticketfu.dto.request.PurchaseTicketRequest;
import com.ismael.ticketfu.dto.request.ValidateTicketRequest;
import com.ismael.ticketfu.dto.response.TicketResponse;
import com.ismael.ticketfu.dto.response.TicketValidationResponse;
import com.ismael.ticketfu.entity.Ticket;

import com.ismael.ticketfu.service.TicketPdfService;
import com.ismael.ticketfu.service.TicketQrService;
import com.ismael.ticketfu.service.TicketService;
import java.io.IOException;
import jakarta.persistence.Access;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//AQUI EL CONTROLLER NO DEBERIA QUE EL USUARIO MANDE DATOS
//PUES HABRIA FALLAS O COSAS QUE NO DEBE HACER, POR ESO SE IMPLEMENTARA
// EN GTO VARIAS COSAS Y LA LOGICA SE LLEVARA EN Service

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor

public class TicketController {

    private final TicketService ticketService;

    private final TicketQrService ticketQrService;

    private final TicketPdfService ticketPdfService; // O inyéctalo en el constructor con RequiredArgsConstructor

    // Comprar un boleto
    @PostMapping("/purchase")
    public ResponseEntity<TicketResponse> purchaseTicket(
            @Valid @RequestBody PurchaseTicketRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Extraemos el username email
        String userEmail = userDetails.getUsername();

        // Le pasamos la solicitud y la identidad real del comprador al servicio
        TicketResponse response = ticketService.purchaseTicket(request, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Consultar un boleto
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id){
        return ResponseEntity.ok(ticketService.getTicket(id));
    }

    // Verificar un QR
    @PostMapping("/validate")
    public ResponseEntity<TicketValidationResponse> validateTicket(
            @Valid @RequestBody ValidateTicketRequest request){

        return ResponseEntity.ok(ticketService.validateQr(request.getQrCode()));
    }

    // Cancelar un boleto
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelTicket(@PathVariable Long id){

        ticketService.cancelTicket(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCode(@RequestParam("text") String text) {
        try {
            byte[] qrImage = ticketQrService.generateQRCodeImage(text, 250, 250);
            return ResponseEntity.ok().contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE)).body(qrImage);
        } catch (WriterException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadTicketPdf(@PathVariable Long id) {
        Ticket ticket = ticketService.getById(id);
        byte[] pdfBytes = ticketPdfService.generateTicketPdf(ticket);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=ticket_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
