package br.com.joao.customerspanel.services;

import br.com.joao.customerspanel.domain.customer.*;
import br.com.joao.customerspanel.exceptions.InvalidArgumentException;
import br.com.joao.customerspanel.exceptions.ResourceNotFoundException;
import br.com.joao.customerspanel.infra.auth.TokenService;
import br.com.joao.customerspanel.repositories.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public CustomerService(CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder,
                           TokenService tokenService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
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

    public CustomerBaseInfoDTO findCustomerBaseInfo(String id) {
        return customerRepository.findById(id).map(x -> new CustomerBaseInfoDTO(
                x.getId(),
                x.getFirstName(),
                x.getLastName(),
                x.getEmail(),
                x.getAvatar())
        ).orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + id));
    }

    public List<CustomerBaseInfoDTO> findAll() {
        return customerRepository.findAll().stream().map(x -> new CustomerBaseInfoDTO(
                x.getId(),
                x.getFirstName(),
                x.getLastName(),
                x.getEmail(),
                x.getAvatar()
        )).toList();
    }

}
