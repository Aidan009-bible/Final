package com.foodmanagement.foodmanagement.dto;

import java.util.Arrays;

import com.foodmanagement.foodmanagement.entity.enums.Role;

public record SignUpDto (
    String name,
    String email,
    char[] password,
    String address,
    Role role
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignUpDto signUpDto = (SignUpDto) o;
        return name.equals(signUpDto.name) &&
               email.equals(signUpDto.email) &&
               Arrays.equals(password, signUpDto.password) &&
               address.equals(signUpDto.address) &&
               role == signUpDto.role;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{name, email, Arrays.hashCode(password), address, role});
    }

    @Override
    public String toString() {
        return "SignUpDto[name=" + name + ", email=" + email + ", password=***, address=" + address + ", role=" + role + "]";
    }
}
