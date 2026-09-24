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

import com.exemplo.app.model.Semestre;
import com.exemplo.app.model.Turma;
import com.exemplo.app.service.SemestreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/semestres")
@RequiredArgsConstructor
public class SemestreController {

    private final SemestreService semestreService;

    @GetMapping
    public List<Semestre> listar() {
        return semestreService.listar();
    }

    @GetMapping("/{codigo}")
    public Semestre buscar(@PathVariable Integer codigo) {
        return semestreService.buscar(codigo);
    }

    @GetMapping("/{codigo}/turmas")
    public List<Turma> listarTurmas(@PathVariable Integer codigo) {
        return semestreService.listarTurmas(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Semestre criar(@RequestBody @Valid Semestre semestre) {
        return semestreService.criar(semestre);
    }

    @PutMapping("/{codigo}")
    public Semestre atualizar(@PathVariable Integer codigo, @RequestBody @Valid Semestre semestre) {
        return semestreService.atualizar(codigo, semestre);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer codigo) {
        semestreService.remover(codigo);
    }
}
