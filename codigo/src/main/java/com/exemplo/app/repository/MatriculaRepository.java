package com.exemplo.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.Matricula;
import com.exemplo.app.model.enums.StatusMatricula;
import com.exemplo.app.model.enums.TipoMatricula;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    List<Matricula> findByAlunoCodigo(Integer alunoCodigo);

    List<Matricula> findByTurmaCodigoAndStatusMatricula(Integer turmaCodigo, StatusMatricula status);

    boolean existsByAlunoCodigoAndTurmaCodigoAndStatusMatricula(Integer alunoCodigo, Integer turmaCodigo, StatusMatricula status);

    long countByAlunoCodigoAndTurmaSemestreCodigoAndTipoMatriculaAndStatusMatricula(
            Integer alunoCodigo, Integer semestreCodigo, TipoMatricula tipo, StatusMatricula status);
}
