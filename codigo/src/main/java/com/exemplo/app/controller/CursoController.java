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

import com.exemplo.app.model.Curso;
import com.exemplo.app.service.CursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public List<Curso> listar() {
        return cursoService.listar();
    }

    @GetMapping("/{codigo}")
    public Curso buscar(@PathVariable Integer codigo) {
        return cursoService.buscar(codigo);
    }

    @PostMapping("/{codigo}/disciplinas/{disciplinaCodigo}")
    public Curso adicionarDisciplina(@PathVariable Integer codigo, @PathVariable Integer disciplinaCodigo) {
        return cursoService.adicionarDisciplina(codigo, disciplinaCodigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Curso criar(@RequestBody @Valid Curso curso) {
        return cursoService.criar(curso);
    }

    @PutMapping("/{codigo}")
    public Curso atualizar(@PathVariable Integer codigo, @RequestBody @Valid Curso curso) {
        return cursoService.atualizar(codigo, curso);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer codigo) {
        cursoService.remover(codigo);
    }
}
