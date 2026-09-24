package com.exemplo.app.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull
    private Integer ano;

    @NotNull
    @Min(1)
    @Max(2)
    private Integer semestre;

    @NotNull
    private LocalDate inicioMatriculas;

    @NotNull
    private LocalDate finalMatriculas;

    @JsonIgnore
    @OneToMany(mappedBy = "semestre")
    private List<Turma> turmas = new ArrayList<>();

    public boolean periodoMatriculaAberto(LocalDate data) {
        return !data.isBefore(inicioMatriculas) && !data.isAfter(finalMatriculas);
    }
}
