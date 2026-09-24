package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Integer> {
}
