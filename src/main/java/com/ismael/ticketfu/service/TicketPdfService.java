package com.ismael.ticketfu.service;

import com.google.zxing.WriterException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ismael.ticketfu.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class TicketPdfService {

    private final TicketQrService ticketQrService;

    // Paleta de colores basada en el estilo Ticketmaster
    private static final Color COLOR_PRIMARY_BLUE = new Color(0, 156, 222);
    private static final Color COLOR_DARK_GRAY = new Color(30, 30, 30);
    private static final Color COLOR_LIGHT_GRAY = new Color(245, 245, 245);
    @Transactional
    public byte[] generateTicketPdf(Ticket ticket) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Tamaño horizontal ajustado estilo boleto
            Document document = new Document(PageSize.A4, 20, 20, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            // Configuración de fuentes
            Font headerBrandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 24, Color.WHITE);
            Font headerSubFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_DARK_GRAY);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_PRIMARY_BLUE);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_DARK_GRAY);
            Font boldValueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_DARK_GRAY);
            Font footerTextFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY);

            // ==========================================
            // 1. CONTENEDOR PRINCIPAL DEL BOLETO
            // ==========================================
            PdfPTable ticketContainer = new PdfPTable(1);
            ticketContainer.setWidthPercentage(100);

            // ------------------------------------------
            // 1.1 ENCABEZADO AZUL (BRANDING)
            // ------------------------------------------
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60, 40});

            PdfPCell brandCell = new PdfPCell(new Phrase("ticketfu", headerBrandFont));
            brandCell.setBorder(Rectangle.NO_BORDER);
            brandCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            brandCell.setPadding(10);

            PdfPCell rightHeaderCell = new PdfPCell(new Phrase("Esta es tu entrada digital\nConserva este boleto para el acceso", headerSubFont));
            rightHeaderCell.setBorder(Rectangle.NO_BORDER);
            rightHeaderCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            rightHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rightHeaderCell.setPadding(10);

            headerTable.addCell(brandCell);
            headerTable.addCell(rightHeaderCell);

            PdfPCell headerContainerCell = new PdfPCell(headerTable);
            headerContainerCell.setBackgroundColor(COLOR_PRIMARY_BLUE);
            headerContainerCell.setPadding(5);
            headerContainerCell.setBorder(Rectangle.NO_BORDER);
            ticketContainer.addCell(headerContainerCell);

            // ------------------------------------------
            // 1.2 CUERPO PRINCIPAL (INFO Y QR)
            // ------------------------------------------
            PdfPTable bodyTable = new PdfPTable(3);
            bodyTable.setWidthPercentage(100);
            bodyTable.setWidths(new float[]{25, 45, 30}); // Columna Pug, Info Evento, QR

            // --- COLUMNA 1: Imagen del Pug Feliz ---
            PdfPTable leftColumnTable = new PdfPTable(1);

            // Cargar imagen desde src/main/resources/static/Pug_marca_agua.png
            org.springframework.core.io.Resource resource =
                    new org.springframework.core.io.ClassPathResource("static/Pug_marca_agua.png");

            Image pugImage = Image.getInstance(resource.getURL());
            pugImage.setAlignment(Element.ALIGN_CENTER);
            pugImage.scaleToFit(120, 150); // Ajusta la escala según tus preferencias

            PdfPCell pugImageCell = new PdfPCell(pugImage);
            pugImageCell.setBorder(Rectangle.NO_BORDER);
            pugImageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            leftColumnTable.addCell(pugImageCell);

            PdfPCell leftContainerCell = new PdfPCell(leftColumnTable);
            leftContainerCell.setBackgroundColor(COLOR_LIGHT_GRAY);
            leftContainerCell.setPadding(5);
            leftContainerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            leftContainerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            // --- COLUMNA 2: Detalles del Evento y Usuario ---
            PdfPTable infoTable = new PdfPTable(1);
            infoTable.setWidthPercentage(100);

            // Nombre del Evento
            infoTable.addCell(createCell("EVENTO", labelFont, false));
            infoTable.addCell(createCell(ticket.getEvent().getName().toUpperCase(), titleFont, false));

            // Comprador & Ticket ID
            infoTable.addCell(createCell("ASISTENTE", labelFont, false));
            infoTable.addCell(createCell(ticket.getUser().getEmail(), valueFont, false));

            infoTable.addCell(createCell("ID DE BOLETO: #" + ticket.getId(), labelFont, false));

            // Fecha y Lugar
            infoTable.addCell(createCell("FECHA Y HORA", labelFont, false));
            infoTable.addCell(createCell(String.valueOf(ticket.getEvent().getLocalDateTime()), boldValueFont, false));

            infoTable.addCell(createCell("LUGAR", labelFont, false));
            infoTable.addCell(createCell(ticket.getEvent().getVenue(), valueFont, false));

            // Estado y Precio
            infoTable.addCell(createCell("PRECIO: $" + ticket.getEvent().getPrice() + " | ESTADO: " + ticket.getTicketStatus(), labelFont, false));

            PdfPCell centerContainerCell = new PdfPCell(infoTable);
            centerContainerCell.setPadding(10);

            // --- COLUMNA 3: Código QR ---
            PdfPTable qrTable = new PdfPTable(1);
            qrTable.setWidthPercentage(100);

            byte[] qrBytes = ticketQrService.generateQRCodeImage(ticket.getQrCode(), 180, 180);
            Image qrImage = Image.getInstance(qrBytes);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.scaleToFit(140, 140);

            PdfPCell qrImageCell = new PdfPCell(qrImage);
            qrImageCell.setBorder(Rectangle.NO_BORDER);
            qrImageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrTable.addCell(qrImageCell);

            PdfPCell qrContainerCell = new PdfPCell(qrTable);
            qrContainerCell.setPadding(10);
            qrContainerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            qrContainerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Agregar las 3 columnas a la tabla del cuerpo
            bodyTable.addCell(leftContainerCell);
            bodyTable.addCell(centerContainerCell);
            bodyTable.addCell(qrContainerCell);

            // Agregar cuerpo al contenedor general
            PdfPCell bodyContainerCell = new PdfPCell(bodyTable);
            bodyContainerCell.setPadding(0);
            ticketContainer.addCell(bodyContainerCell);

            // ------------------------------------------
            // 1.3 PIE DE PÁGINA INTERNO
            // ------------------------------------------
            Paragraph footerText = new Paragraph("Presenta este código QR en la entrada del evento. Este boleto es personal e transferible únicamente según las políticas de Ticketfu.", footerTextFont);
            footerText.setAlignment(Element.ALIGN_CENTER);

            PdfPCell footerCell = new PdfPCell(footerText);
            footerCell.setPadding(8);
            footerCell.setBackgroundColor(COLOR_LIGHT_GRAY);
            ticketContainer.addCell(footerCell);

            // Dibujar la tabla completa en el documento
            document.add(ticketContainer);

            document.close();
            return out.toByteArray();

        } catch (DocumentException | IOException | WriterException e) {
            throw new RuntimeException("Error al generar el PDF del boleto.", e);
        }
    }
    @Transactional
    // Método auxiliar para construir celdas de texto limpias
    private PdfPCell createCell(String text, Font font, boolean hasBorder) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        if (!hasBorder) {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        cell.setPaddingBottom(3);
        return cell;
    }
}