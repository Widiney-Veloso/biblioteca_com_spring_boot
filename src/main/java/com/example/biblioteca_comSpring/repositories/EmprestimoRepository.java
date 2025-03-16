package com.example.biblioteca_comSpring.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.biblioteca_comSpring.dominio.Emprestimo;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID>{
    
}
