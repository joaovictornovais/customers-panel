package br.com.joao.customerspanel.controllers;

import br.com.joao.customerspanel.domain.customer.LoginRequestDTO;
import br.com.joao.customerspanel.domain.customer.RegisterRequestDTO;
import br.com.joao.customerspanel.domain.customer.TokenResponseDTO;
import br.com.joao.customerspanel.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.register(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.login(loginRequestDTO));
    }

}
