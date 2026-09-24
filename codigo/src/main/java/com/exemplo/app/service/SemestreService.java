package com.exemplo.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.model.Semestre;
import com.exemplo.app.model.Turma;
import com.exemplo.app.repository.SemestreRepository;
import com.exemplo.app.repository.TurmaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemestreService {

    private final SemestreRepository semestreRepository;
    private final TurmaRepository turmaRepository;

    public List<Semestre> listar() {
        return semestreRepository.findAll();
    }

    public Semestre buscar(Integer codigo) {
        return semestreRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semestre não encontrado"));
    }

    public List<Turma> listarTurmas(Integer codigo) {
        buscar(codigo);
        return turmaRepository.findBySemestreCodigo(codigo);
    }

    public Semestre criar(Semestre semestre) {
        validarPeriodo(semestre);
        semestre.setCodigo(null);
        return semestreRepository.save(semestre);
    }

    @Transactional
    public Semestre atualizar(Integer codigo, Semestre dados) {
        validarPeriodo(dados);
        Semestre semestre = buscar(codigo);
        semestre.setAno(dados.getAno());
        semestre.setSemestre(dados.getSemestre());
        semestre.setInicioMatriculas(dados.getInicioMatriculas());
        semestre.setFinalMatriculas(dados.getFinalMatriculas());
        return semestre;
    }

    public void remover(Integer codigo) {
        Semestre semestre = buscar(codigo);
        if (!semestre.getTurmas().isEmpty()) {
            throw new IllegalStateException("Semestre possui turmas vinculadas");
        }
        semestreRepository.delete(semestre);
    }

    private void validarPeriodo(Semestre semestre) {
        if (semestre.getFinalMatriculas().isBefore(semestre.getInicioMatriculas())) {
            throw new IllegalStateException("O final das matrículas deve ser posterior ao início");
        }
    }
}
