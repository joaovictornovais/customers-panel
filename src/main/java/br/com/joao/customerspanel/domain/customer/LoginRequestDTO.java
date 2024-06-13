package br.com.joao.customerspanel.domain.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "E-mail should not be blank")
        @Email(message = "E-mail should be well formated. Example: your_email@example.com")
        String email,
        @NotBlank(message = "Password should not be blank")
        String password
) {
}
