package com.exemplo.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.Turma;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Integer> {

    List<Turma> findByProfessorCodigo(Integer professorCodigo);

    List<Turma> findBySemestreCodigo(Integer semestreCodigo);
}
