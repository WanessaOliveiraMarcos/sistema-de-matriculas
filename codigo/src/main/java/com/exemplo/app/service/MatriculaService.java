package com.exemplo.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.dto.MatriculaRequest;
import com.exemplo.app.model.Aluno;
import com.exemplo.app.model.Matricula;
import com.exemplo.app.model.Notificacao;
import com.exemplo.app.model.Turma;
import com.exemplo.app.model.enums.StatusAluno;
import com.exemplo.app.model.enums.StatusMatricula;
import com.exemplo.app.model.enums.StatusNotificacao;
import com.exemplo.app.model.enums.TipoMatricula;
import com.exemplo.app.model.enums.TipoNotificacao;
import com.exemplo.app.repository.MatriculaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    public static final int MAX_OBRIGATORIAS = 4;
    public static final int MAX_OPTATIVAS = 2;

    private final MatriculaRepository matriculaRepository;
    private final AlunoService alunoService;
    private final TurmaService turmaService;
    private final ApplicationEventPublisher eventPublisher;

    public List<Matricula> listar() {
        return matriculaRepository.findAll();
    }

    public Matricula buscar(Integer codigo) {
        return matriculaRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula não encontrada"));
    }

    public List<Matricula> listarPorAluno(Integer alunoCodigo) {
        alunoService.buscar(alunoCodigo);
        return matriculaRepository.findByAlunoCodigo(alunoCodigo);
    }

    @Transactional
    public Matricula matricular(MatriculaRequest request) {
        Aluno aluno = alunoService.buscar(request.alunoCodigo());
        Turma turma = turmaService.buscar(request.turmaCodigo());

        if (matriculaRepository.existsByAlunoCodigoAndTurmaCodigoAndStatusMatricula(
                aluno.getCodigo(), turma.getCodigo(), StatusMatricula.ATIVA)) {
            throw new IllegalStateException("Aluno já matriculado nesta turma");
        }

        // O tipo (obrigatória/optativa) é definido pela secretaria na disciplina
        TipoMatricula tipo = turma.getDisciplina().getTipoMatricula();
        if (tipo == null) {
            throw new IllegalStateException("A disciplina ainda não possui tipo definido pela secretaria");
        }

        long jaMatriculadas = matriculaRepository.countByAlunoCodigoAndTurmaSemestreCodigoAndTipoMatriculaAndStatusMatricula(
                aluno.getCodigo(), turma.getSemestre().getCodigo(), tipo, StatusMatricula.ATIVA);
        int limite = tipo == TipoMatricula.OBRIGATORIA ? MAX_OBRIGATORIAS : MAX_OPTATIVAS;
        if (jaMatriculadas >= limite) {
            throw new IllegalStateException("Limite de " + limite + " disciplinas "
                    + tipo.name().toLowerCase() + "s atingido no semestre");
        }

        Matricula matricula = new Matricula();
        matricula.setTipoMatricula(tipo);
        matricula.matricular(aluno, turma);
        Matricula salva = matriculaRepository.save(matricula);

        notificar(aluno, TipoNotificacao.MATRICULA,
                "Matrícula confirmada em " + turma.getDisciplina().getNome());
        return salva;
    }

    @Transactional
    public Matricula cancelar(Integer codigo) {
        Matricula matricula = buscar(codigo);
        if (matricula.getStatusMatricula() == StatusMatricula.INATIVA) {
            throw new IllegalStateException("Matrícula já cancelada");
        }
        if (matricula.getAluno().getStatus() != StatusAluno.ATIVO) {
            throw new IllegalStateException("Apenas alunos ativos podem cancelar matrículas");
        }
        if (!matricula.getTurma().getSemestre().periodoMatriculaAberto(LocalDate.now())) {
            throw new IllegalStateException("Cancelamento permitido apenas durante o período de matrículas");
        }
        matricula.setStatusMatricula(StatusMatricula.INATIVA);

        notificar(matricula.getAluno(), TipoNotificacao.CANCELAMENTO,
                "Matrícula cancelada em " + matricula.getTurma().getDisciplina().getNome());
        return matricula;
    }

    // Publica o evento de notificação; o NotificacaoListener persiste a
    // notificação para a aba do usuário. O MatriculaService não conhece nem
    // SistemaCobranca nem o repositório de notificações.
    private void notificar(Aluno aluno, TipoNotificacao tipo, String mensagem) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDestinatario(aluno);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(tipo);
        notificacao.setStatus(StatusNotificacao.PENDENTE);
        notificacao.setDataCriacao(LocalDateTime.now());
        eventPublisher.publishEvent(notificacao);
    }
}
