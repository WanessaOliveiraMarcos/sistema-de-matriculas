package com.exemplo.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.model.Professor;
import com.exemplo.app.model.Turma;
import com.exemplo.app.repository.ProfessorRepository;
import com.exemplo.app.repository.TurmaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UsuarioService usuarioService;
    private final TurmaRepository turmaRepository;

    public List<Professor> listar() {
        return professorRepository.findAll();
    }

    public Professor buscar(Integer codigo) {
        return professorRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado"));
    }

    public List<Turma> listarTurmas(Integer codigo) {
        buscar(codigo);
        return turmaRepository.findByProfessorCodigo(codigo);
    }

    public Professor criar(Professor professor) {
        usuarioService.prepararNovo(professor);
        return professorRepository.save(professor);
    }

    @Transactional
    public Professor atualizar(Integer codigo, Professor dados) {
        Professor professor = buscar(codigo);
        usuarioService.atualizarDados(professor, dados);
        professor.setTitulacao(dados.getTitulacao());
        return professor;
    }

    public void remover(Integer codigo) {
        Professor professor = buscar(codigo);
        if (!professor.getTurmas().isEmpty()) {
            throw new IllegalStateException("Professor possui turmas vinculadas");
        }
        professorRepository.delete(professor);
    }
}
