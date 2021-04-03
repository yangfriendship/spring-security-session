package com.security.jwt.domain;

public enum Role {

    USER("ROLE_USER"),MANAGER("ROLE_MANAGER"),ADMIN("ROLE_ADMIN");

    private final String code;

    Role(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;

    }
}
