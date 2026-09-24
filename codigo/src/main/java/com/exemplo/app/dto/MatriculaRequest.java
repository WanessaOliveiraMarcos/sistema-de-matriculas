package com.exemplo.app.dto;

import jakarta.validation.constraints.NotNull;

// O tipo da matrícula (obrigatória/optativa) é definido pela secretaria na
// disciplina, não pelo aluno — por isso não faz parte do request.
public record MatriculaRequest(
        @NotNull Integer alunoCodigo,
        @NotNull Integer turmaCodigo) {
}
