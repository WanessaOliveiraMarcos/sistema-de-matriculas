package com.exemplo.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.model.Curso;
import com.exemplo.app.model.Disciplina;
import com.exemplo.app.repository.CursoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final DisciplinaService disciplinaService;

    public List<Curso> listar() {
        return cursoRepository.findAll();
    }

    public Curso buscar(Integer codigo) {
        return cursoRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
    }

    public Curso criar(Curso curso) {
        curso.setCodigo(null);
        curso.getDisciplinas().clear();
        return cursoRepository.save(curso);
    }

    @Transactional
    public Curso atualizar(Integer codigo, Curso dados) {
        Curso curso = buscar(codigo);
        curso.setNome(dados.getNome());
        curso.setCreditos(dados.getCreditos());
        return curso;
    }

    @Transactional
    public Curso adicionarDisciplina(Integer codigo, Integer disciplinaCodigo) {
        Curso curso = buscar(codigo);
        Disciplina disciplina = disciplinaService.buscar(disciplinaCodigo);
        if (disciplina.getCurso() != null && !disciplina.getCurso().equals(curso)) {
            throw new IllegalStateException("Disciplina já pertence a outro curso");
        }
        if (!curso.adicionarDisciplina(disciplina)) {
            throw new IllegalStateException("Disciplina já adicionada ao curso");
        }
        return curso;
    }

    public void remover(Integer codigo) {
        cursoRepository.delete(buscar(codigo));
    }
}
