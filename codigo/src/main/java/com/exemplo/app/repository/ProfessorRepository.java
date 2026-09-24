package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
}
