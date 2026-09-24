package com.exemplo.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.model.Secretario;
import com.exemplo.app.service.SecretarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secretarios")
@RequiredArgsConstructor
public class SecretarioController {

    private final SecretarioService secretarioService;

    @GetMapping
    public List<Secretario> listar() {
        return secretarioService.listar();
    }

    @GetMapping("/{codigo}")
    public Secretario buscar(@PathVariable Integer codigo) {
        return secretarioService.buscar(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Secretario criar(@RequestBody @Valid Secretario secretario) {
        return secretarioService.criar(secretario);
    }

    @PutMapping("/{codigo}")
    public Secretario atualizar(@PathVariable Integer codigo, @RequestBody @Valid Secretario secretario) {
        return secretarioService.atualizar(codigo, secretario);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer codigo) {
        secretarioService.remover(codigo);
    }
}
