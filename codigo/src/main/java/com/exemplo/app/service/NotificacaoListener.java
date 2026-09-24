package com.exemplo.app.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.exemplo.app.model.Notificacao;
import com.exemplo.app.model.enums.StatusNotificacao;
import com.exemplo.app.repository.NotificacaoRepository;

import lombok.RequiredArgsConstructor;

// Reage ao evento de notificação publicado pelo MatriculaService, persistindo a
// notificação para que ela fique disponível na aba do usuário. Como não há um
// sistema externo de cobrança, a persistência já representa a "entrega" da
// notificação (status ENVIADA).
@Component
@RequiredArgsConstructor
public class NotificacaoListener {

    private final NotificacaoRepository notificacaoRepository;

    @EventListener
    public void aoReceberNotificacao(Notificacao notificacao) {
        notificacao.setStatus(StatusNotificacao.ENVIADA);
        notificacaoRepository.save(notificacao);
    }
}