package com.exemplo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemplo.app.model.Secretario;

@Repository
public interface SecretarioRepository extends JpaRepository<Secretario, Integer> {
}
