package br.com.joao.customerspanel.controllers;

import br.com.joao.customerspanel.domain.customer.CustomerBaseInfoDTO;
import br.com.joao.customerspanel.domain.customer.CustomerDTO;
import br.com.joao.customerspanel.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerBaseInfoDTO> findCustomerById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.findCustomerBaseInfoById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerBaseInfoDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerBaseInfoDTO> edit(
            @PathVariable String id, @RequestBody @Valid CustomerDTO customerDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.edit(id, customerDTO));
    }

}
