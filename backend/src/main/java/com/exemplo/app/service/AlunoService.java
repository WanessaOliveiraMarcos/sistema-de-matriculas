package com.exemplo.app.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.exemplo.app.model.Aluno;
import com.exemplo.app.repository.AlunoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioService usuarioService;

    public List<Aluno> listar() {
        return alunoRepository.findAll();
    }

    public Aluno buscar(Integer codigo) {
        return alunoRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado"));
    }

    public Aluno criar(Aluno aluno) {
        usuarioService.prepararNovo(aluno);
        return alunoRepository.save(aluno);
    }

    @Transactional
    public Aluno atualizar(Integer codigo, Aluno dados) {
        Aluno aluno = buscar(codigo);
        usuarioService.atualizarDados(aluno, dados);
        if (dados.getStatus() != null) {
            aluno.setStatus(dados.getStatus());
        }
        return aluno;
    }

    public void remover(Integer codigo) {
        Aluno aluno = buscar(codigo);
        if (!aluno.getMatriculas().isEmpty()) {
            throw new IllegalStateException("Aluno possui matrículas vinculadas");
        }
        alunoRepository.delete(aluno);
    }
}
