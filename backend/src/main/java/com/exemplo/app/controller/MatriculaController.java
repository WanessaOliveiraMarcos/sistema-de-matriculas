package com.exemplo.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.app.dto.MatriculaRequest;
import com.exemplo.app.model.Matricula;
import com.exemplo.app.service.MatriculaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @GetMapping
    public List<Matricula> listar(@RequestParam(required = false) Integer alunoCodigo) {
        return alunoCodigo == null ? matriculaService.listar() : matriculaService.listarPorAluno(alunoCodigo);
    }

    @GetMapping("/{codigo}")
    public Matricula buscar(@PathVariable Integer codigo) {
        return matriculaService.buscar(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Matricula matricular(@RequestBody @Valid MatriculaRequest request) {
        return matriculaService.matricular(request);
    }

    @PatchMapping("/{codigo}/cancelar")
    public Matricula cancelar(@PathVariable Integer codigo) {
        return matriculaService.cancelar(codigo);
    }
}
