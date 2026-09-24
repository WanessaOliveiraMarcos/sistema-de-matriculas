package com.exemplo.app.model;

import java.time.LocalDateTime;

import com.exemplo.app.model.enums.StatusNotificacao;
import com.exemplo.app.model.enums.TipoNotificacao;

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

// Notificação gerada por eventos de domínio (matrícula/cancelamento). O
// destinatário é qualquer Usuario (aluno, professor, secretaria), o que
// permite reutilizar a entidade para outros tipos de notificação no futuro.
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_codigo", nullable = false)
    private Usuario destinatario;

    private String mensagem;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    @Enumerated(EnumType.STRING)
    private StatusNotificacao status;

    private LocalDateTime dataCriacao;
}