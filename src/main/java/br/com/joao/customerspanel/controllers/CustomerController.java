package br.com.joao.customerspanel.controllers;

import br.com.joao.customerspanel.domain.customer.CustomerBaseInfoDTO;
import br.com.joao.customerspanel.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.status(HttpStatus.OK).body(customerService.findCustomerBaseInfo(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerBaseInfoDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.findAll());
    }

}
