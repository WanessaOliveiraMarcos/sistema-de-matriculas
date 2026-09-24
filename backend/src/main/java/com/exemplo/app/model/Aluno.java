package com.exemplo.app.model;

import java.util.ArrayList;
import java.util.List;

import com.exemplo.app.model.enums.StatusAluno;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Aluno extends Usuario {

    @Enumerated(EnumType.STRING)
    private StatusAluno status = StatusAluno.ATIVO;

    @JsonIgnore
    @OneToMany(mappedBy = "aluno")
    private List<Matricula> matriculas = new ArrayList<>();
}
