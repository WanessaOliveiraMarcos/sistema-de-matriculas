package com.exemplo.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.exemplo.app.model.Secretario;
import com.exemplo.app.repository.SecretarioRepository;
import com.exemplo.app.service.UsuarioService;

import lombok.RequiredArgsConstructor;

// Cria um secretário inicial para permitir o primeiro login, já que todas as rotas exigem autenticação
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final SecretarioRepository secretarioRepository;
    private final UsuarioService usuarioService;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.senha}")
    private String senha;

    @Override
    public void run(String... args) {
        if (secretarioRepository.count() > 0) {
            return;
        }
        Secretario admin = new Secretario();
        admin.setNome("Administrador");
        admin.setEmail(email);
        admin.setSenha(senha);
        admin.setCargo("Administrador");
        usuarioService.prepararNovo(admin);
        secretarioRepository.save(admin);
    }
}
