package com.exemplo.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exemplo.app.model.Notificacao;
import com.exemplo.app.repository.NotificacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public List<Notificacao> listarPorUsuario(Integer usuarioCodigo) {
        return notificacaoRepository.findByDestinatarioCodigoOrderByDataCriacaoDesc(usuarioCodigo);
    }
}