package com.exemplo.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.model.Disciplina;
import com.exemplo.app.repository.DisciplinaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    public List<Disciplina> listar() {
        return disciplinaRepository.findAll();
    }

    public Disciplina buscar(Integer codigo) {
        return disciplinaRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada"));
    }

    public Disciplina criar(Disciplina disciplina) {
        disciplina.setCodigo(null);
        return disciplinaRepository.save(disciplina);
    }

    @Transactional
    public Disciplina atualizar(Integer codigo, Disciplina dados) {
        Disciplina disciplina = buscar(codigo);
        disciplina.setNome(dados.getNome());
        disciplina.setTipoMatricula(dados.getTipoMatricula());
        return disciplina;
    }

    @Transactional
    public void remover(Integer codigo) {
        Disciplina disciplina = buscar(codigo);
        if (!disciplina.getTurmas().isEmpty()) {
            throw new IllegalStateException("Disciplina possui turmas vinculadas");
        }
        if (disciplina.getCurso() != null) {
            disciplina.getCurso().getDisciplinas().remove(disciplina);
        }
        disciplinaRepository.delete(disciplina);
    }
}
