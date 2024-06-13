package br.com.joao.customerspanel.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    USER("user"),
    ADMIN("admin");

    private final String role;

}
