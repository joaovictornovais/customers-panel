package br.com.joao.customerspanel.domain.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank(message = "First Name should not be blank")
        String firstName,
        @NotBlank(message = "Last Name should not be blank")
        String lastName,
        @Email(message = "E-mail should be well formated. Example: your_email@example.com")
        @NotBlank(message = "Email should not be blank")
        String email,
        @NotBlank(message = "Password should not be blank")
        String password) {
}
