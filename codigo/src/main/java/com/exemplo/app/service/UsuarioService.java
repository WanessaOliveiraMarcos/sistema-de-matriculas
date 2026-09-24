package com.exemplo.app.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.dto.LoginResponse;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.repository.UsuarioRepository;
import com.exemplo.app.security.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginResponse autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .filter(u -> u.autenticar(senha, passwordEncoder))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));
        return new LoginResponse(tokenService.gerar(usuario), usuario.getCodigo(), usuario.getNome(),
                usuario.getEmail(), usuario.getPerfil());
    }

    // Valida e-mail único e criptografa a senha de um usuário novo
    public void prepararNovo(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        usuario.setCodigo(null);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
    }

    // Copia os dados comuns de Usuario, validando e-mail único e criptografando a nova senha
    public void atualizarDados(Usuario usuario, Usuario dados) {
        if (!usuario.getEmail().equalsIgnoreCase(dados.getEmail()) && usuarioRepository.existsByEmail(dados.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        usuario.setNome(dados.getNome());
        usuario.setEmail(dados.getEmail());
        usuario.setSenha(passwordEncoder.encode(dados.getSenha()));
    }
}
