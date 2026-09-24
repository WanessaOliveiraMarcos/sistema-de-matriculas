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

import com.exemplo.app.model.Professor;
import com.exemplo.app.model.Turma;
import com.exemplo.app.service.ProfessorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    public List<Professor> listar() {
        return professorService.listar();
    }

    @GetMapping("/{codigo}")
    public Professor buscar(@PathVariable Integer codigo) {
        return professorService.buscar(codigo);
    }

    @GetMapping("/{codigo}/turmas")
    public List<Turma> listarTurmas(@PathVariable Integer codigo) {
        return professorService.listarTurmas(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Professor criar(@RequestBody @Valid Professor professor) {
        return professorService.criar(professor);
    }

    @PutMapping("/{codigo}")
    public Professor atualizar(@PathVariable Integer codigo, @RequestBody @Valid Professor professor) {
        return professorService.atualizar(codigo, professor);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer codigo) {
        professorService.remover(codigo);
    }
}
