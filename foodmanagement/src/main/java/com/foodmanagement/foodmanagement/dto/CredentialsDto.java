package com.foodmanagement.foodmanagement.dto;

import java.util.Arrays;

public record CredentialsDto (String email, char[] password) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CredentialsDto that = (CredentialsDto) o;
        return email.equals(that.email) && Arrays.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{email, Arrays.hashCode(password)});
    }

    @Override
    public String toString() {
        return "CredentialsDto[email=" + email + ", password=***]";
    }
}
