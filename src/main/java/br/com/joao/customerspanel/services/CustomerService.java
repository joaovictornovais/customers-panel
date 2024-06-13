package br.com.joao.customerspanel.services;

import br.com.joao.customerspanel.domain.customer.*;
import br.com.joao.customerspanel.exceptions.InvalidArgumentException;
import br.com.joao.customerspanel.exceptions.ResourceNotFoundException;
import br.com.joao.customerspanel.infra.auth.TokenService;
import br.com.joao.customerspanel.repositories.CustomerRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public CustomerService(CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           TokenService tokenService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public TokenResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        try {
            findByEmail(registerRequestDTO.email());
        } catch (ResourceNotFoundException e) {
            Customer customer = new Customer(registerRequestDTO);
            customer.setRole(UserRole.USER);
            customer.setPassword(passwordEncoder.encode(registerRequestDTO.password()));
            customerRepository.save(customer);

            String token = tokenService.generateToken(customer);
            return new TokenResponseDTO(customer.getFirstName(), token);
        }
        throw new InvalidArgumentException("E-mail already registered");
    }

    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Customer customer = findByEmail(loginRequestDTO.email());
        if (passwordEncoder.matches(loginRequestDTO.password(), customer.getPassword())) {
            String token = tokenService.generateToken(customer);
            return new TokenResponseDTO(customer.getFirstName(), token);
        }
        throw new InvalidArgumentException("Authentication failed. Wrong e-mail or password, please try again.");
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}
