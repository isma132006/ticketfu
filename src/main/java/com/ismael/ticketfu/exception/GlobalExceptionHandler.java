package com.ismael.ticketfu.exception;

import com.ismael.ticketfu.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Manejo de Errores de Validación (HTTP 400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "La solicitud contiene datos inválidos",
                errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    //Manejar de IllegalArgumentException pues pueden enviar cosas que ya existen
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> hadleIllegalArgumentException(IllegalArgumentException ex){
        //obtener el msj de ex,
        String msj = ex.getMessage();
        //instanciar ErrorResponse
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                msj);
        //return el error con el onjeto y el estado Http
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    //Manejar de ResourceNotFoundException para cosas no encontradas
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex){
        //obtener el msj de ex,
        String msj = ex.getMessage();
        //instanciar ErrorResponse
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                msj);
        //return el error con el onjeto y el estado Http
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    // 2. Captura genérica para errores no previstos (HTTP 500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Excepción no controlada: ", ex); // Guardar en log interno

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ha ocurrido un error interno en el servidor. Por favor intente más tarde."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}