package com.exemplo.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.dto.TurmaRequest;
import com.exemplo.app.model.Aluno;
import com.exemplo.app.model.Turma;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.service.TurmaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @GetMapping
    public List<Turma> listar() {
        return turmaService.listar();
    }

    @GetMapping("/{codigo}")
    public Turma buscar(@PathVariable Integer codigo) {
        return turmaService.buscar(codigo);
    }

    @GetMapping("/{codigo}/alunos")
    public List<Aluno> listarAlunos(@PathVariable Integer codigo, Authentication authentication) {
        // Professor só pode ver alunos das turmas que ele mesmo leciona
        if (authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR"))) {
            Turma turma = turmaService.buscar(codigo);
            Usuario logado = (Usuario) authentication.getPrincipal();
            if (!turma.getProfessor().getCodigo().equals(logado.getCodigo())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Professor só pode consultar alunos das próprias turmas");
            }
        }
        return turmaService.listarAlunos(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Turma criar(@RequestBody @Valid TurmaRequest request) {
        return turmaService.criar(request);
    }

    @PutMapping("/{codigo}")
    public Turma atualizar(@PathVariable Integer codigo, @RequestBody @Valid TurmaRequest request) {
        return turmaService.atualizar(codigo, request);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer codigo) {
        turmaService.remover(codigo);
    }
}
