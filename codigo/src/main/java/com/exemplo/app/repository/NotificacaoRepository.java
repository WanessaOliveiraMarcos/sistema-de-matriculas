package com.exemplo.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.Notificacao;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByDestinatarioCodigoOrderByDataCriacaoDesc(Integer usuarioCodigo);
}