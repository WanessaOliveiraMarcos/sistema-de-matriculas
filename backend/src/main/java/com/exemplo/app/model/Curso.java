package com.exemplo.app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank
    private String nome;

    @NotNull
    @Positive
    private Integer creditos;

    // Composição: as disciplinas pertencem ao curso e são removidas junto com ele
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disciplina> disciplinas = new ArrayList<>();

    public boolean adicionarDisciplina(Disciplina disciplina) {
        if (disciplina == null || disciplinas.contains(disciplina)) {
            return false;
        }
        disciplina.setCurso(this);
        return disciplinas.add(disciplina);
    }
}
