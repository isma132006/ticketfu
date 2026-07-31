package com.ismael.ticketfu.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe proporcionar una dirección de correo electrónico válida")
    private String email;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(
            regexp = "^\\+?[0-9]{8,15}$",
            message = "El número de teléfono debe ser válido y contener entre 8 y 15 dígitos"
    )
    private String phoneNumber;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 1, max = 64, message = "La contraseña debe tener entre 4 y 64 caracteres")
    private String password;
}