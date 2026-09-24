package com.exemplo.app.model;

import java.util.ArrayList;
import java.util.List;

import com.exemplo.app.model.enums.StatusMatricula;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Turma {

    public static final int VAGAS_MAXIMAS = 60;
    public static final int VAGAS_MINIMAS = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_codigo", nullable = false)
    private Professor professor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "disciplina_codigo", nullable = false)
    private Disciplina disciplina;

    @ManyToOne(optional = false)
    @JoinColumn(name = "semestre_codigo", nullable = false)
    private Semestre semestre;

    @JsonIgnore
    @OneToMany(mappedBy = "turma")
    private List<Matricula> matriculas = new ArrayList<>();

    public long getQuantidadeMatriculados() {
        return matriculas.stream()
                .filter(m -> m.getStatusMatricula() == StatusMatricula.ATIVA)
                .count();
    }

    public boolean possuiVagas() {
        return getQuantidadeMatriculados() < VAGAS_MAXIMAS;
    }

    public boolean atingiuMinimo() {
        return getQuantidadeMatriculados() >= VAGAS_MINIMAS;
    }
}
