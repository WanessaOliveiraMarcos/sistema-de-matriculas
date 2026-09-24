package com.exemplo.app.dto;

import com.exemplo.app.model.enums.TipoMatricula;

import jakarta.validation.constraints.NotNull;

public record MatriculaRequest(
        @NotNull Integer alunoCodigo,
        @NotNull Integer turmaCodigo,
        @NotNull TipoMatricula tipoMatricula) {
}
