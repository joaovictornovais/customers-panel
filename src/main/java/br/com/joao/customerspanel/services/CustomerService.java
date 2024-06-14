package br.com.joao.customerspanel.services;

import br.com.joao.customerspanel.domain.customer.*;
import br.com.joao.customerspanel.exceptions.InvalidArgumentException;
import br.com.joao.customerspanel.exceptions.ResourceNotFoundException;
import br.com.joao.customerspanel.infra.auth.TokenService;
import br.com.joao.customerspanel.infra.s3.S3Buckets;
import br.com.joao.customerspanel.infra.s3.S3Service;
import br.com.joao.customerspanel.repositories.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;

    public CustomerService(CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder,
                           TokenService tokenService,
                           S3Service s3Service,
                           S3Buckets s3Buckets) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
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

    public CustomerBaseInfoDTO findCustomerBaseInfoById(String id) {
        return customerRepository.findById(id).map(this::convertToBaseInfo)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + id));
    }

    public List<CustomerBaseInfoDTO> findAll() {
        return customerRepository.findAll().stream().map(this::convertToBaseInfo).toList();
    }

    public CustomerBaseInfoDTO edit(String id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        BeanUtils.copyProperties(customerDTO, customer);
        customer = customerRepository.save(customer);
        return convertToBaseInfo(customer);
    }

    public void uploadAvatar(String id, MultipartFile file) {
        findById(id); // If customer doesn't exist, throws an exception
        String profileImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(
                    s3Buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(id, profileImageId),
                    file.getBytes());
        } catch (IOException e) {
            throw new InvalidArgumentException("Image not compatible");
        }
        updateCustomerProfileImageId(id, profileImageId);
    }

    public byte[] getAvatar(String id) {
        CustomerBaseInfoDTO customer = findCustomerBaseInfoById(id);

        if (customer.profileImageId().isBlank()) {
            throw new ResourceNotFoundException("Customer with ID: " + id + " profile image not found");
        }

        return s3Service.getObject(
                s3Buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customer.id(), customer.profileImageId())
        );
    }
    
    public Customer findById(String id) {
        return customerRepository.findById(id).orElseThrow(() -> 
                new ResourceNotFoundException("User not found with ID " + id));
    }

    private CustomerBaseInfoDTO convertToBaseInfo(Customer customer) {
        return new CustomerBaseInfoDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getProfileImageId()
        );
    }

    private void updateCustomerProfileImageId(String customerId, String profileImageId) {
        customerRepository.updateProfileImageId(customerId, profileImageId);
    }

}
