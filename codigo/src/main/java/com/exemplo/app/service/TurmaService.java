package com.exemplo.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.dto.TurmaRequest;
import com.exemplo.app.model.Aluno;
import com.exemplo.app.model.Matricula;
import com.exemplo.app.model.Turma;
import com.exemplo.app.model.enums.StatusMatricula;
import com.exemplo.app.repository.MatriculaRepository;
import com.exemplo.app.repository.TurmaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;
    private final ProfessorService professorService;
    private final DisciplinaService disciplinaService;
    private final SemestreService semestreService;

    public List<Turma> listar() {
        return turmaRepository.findAll();
    }

    public Turma buscar(Integer codigo) {
        return turmaRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada"));
    }

    public List<Aluno> listarAlunos(Integer codigo) {
        buscar(codigo);
        return matriculaRepository.findByTurmaCodigoAndStatusMatricula(codigo, StatusMatricula.ATIVA)
                .stream()
                .map(Matricula::getAluno)
                .toList();
    }

    public Turma criar(TurmaRequest request) {
        Turma turma = new Turma();
        preencher(turma, request);
        return turmaRepository.save(turma);
    }

    @Transactional
    public Turma atualizar(Integer codigo, TurmaRequest request) {
        Turma turma = buscar(codigo);
        preencher(turma, request);
        return turma;
    }

    public void remover(Integer codigo) {
        Turma turma = buscar(codigo);
        if (!turma.getMatriculas().isEmpty()) {
            throw new IllegalStateException("Turma possui matrículas vinculadas");
        }
        turmaRepository.delete(turma);
    }

    private void preencher(Turma turma, TurmaRequest request) {
        turma.setProfessor(professorService.buscar(request.professorCodigo()));
        turma.setDisciplina(disciplinaService.buscar(request.disciplinaCodigo()));
        turma.setSemestre(semestreService.buscar(request.semestreCodigo()));
    }
}
