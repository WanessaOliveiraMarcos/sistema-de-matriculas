package com.exemplo.app.dto;

import jakarta.validation.constraints.NotNull;

public record TurmaRequest(
        @NotNull Integer professorCodigo,
        @NotNull Integer disciplinaCodigo,
        @NotNull Integer semestreCodigo) {
}
