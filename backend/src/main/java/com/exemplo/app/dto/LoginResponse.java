package com.exemplo.app.dto;

public record LoginResponse(String token, Integer codigo, String nome, String email, String perfil) {
}
