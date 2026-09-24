package com.exemplo.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.model.Secretario;
import com.exemplo.app.repository.SecretarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecretarioService {

    private final SecretarioRepository secretarioRepository;
    private final UsuarioService usuarioService;

    public List<Secretario> listar() {
        return secretarioRepository.findAll();
    }

    public Secretario buscar(Integer codigo) {
        return secretarioRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secretário não encontrado"));
    }

    public Secretario criar(Secretario secretario) {
        usuarioService.prepararNovo(secretario);
        return secretarioRepository.save(secretario);
    }

    @Transactional
    public Secretario atualizar(Integer codigo, Secretario dados) {
        Secretario secretario = buscar(codigo);
        usuarioService.atualizarDados(secretario, dados);
        secretario.setCargo(dados.getCargo());
        return secretario;
    }

    public void remover(Integer codigo) {
        secretarioRepository.delete(buscar(codigo));
    }
}
