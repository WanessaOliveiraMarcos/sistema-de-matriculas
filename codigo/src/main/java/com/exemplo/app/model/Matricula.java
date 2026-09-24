package com.exemplo.app.model;

import java.time.LocalDate;

import com.exemplo.app.model.enums.StatusAluno;
import com.exemplo.app.model.enums.StatusMatricula;
import com.exemplo.app.model.enums.TipoMatricula;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Classe de associação entre Aluno (0..60) e Turma (0..N)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @Enumerated(EnumType.STRING)
    private StatusMatricula statusMatricula;

    @Enumerated(EnumType.STRING)
    private TipoMatricula tipoMatricula;

    private LocalDate data;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_codigo", nullable = false)
    private Aluno aluno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "turma_codigo", nullable = false)
    private Turma turma;

    public void matricular(Aluno aluno, Turma turma) {
        if (aluno.getStatus() != StatusAluno.ATIVO) {
            throw new IllegalStateException("Apenas alunos ativos podem se matricular");
        }
        if (!turma.getSemestre().periodoMatriculaAberto(LocalDate.now())) {
            throw new IllegalStateException("Fora do período de matrículas do semestre");
        }
        if (!turma.possuiVagas()) {
            throw new IllegalStateException("A turma já atingiu o limite de " + Turma.VAGAS_MAXIMAS + " alunos");
        }
        this.aluno = aluno;
        this.turma = turma;
        this.data = LocalDate.now();
        this.statusMatricula = StatusMatricula.ATIVA;
        aluno.getMatriculas().add(this);
        turma.getMatriculas().add(this);
    }
}
