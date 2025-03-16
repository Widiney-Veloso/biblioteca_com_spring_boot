package com.example.biblioteca_comSpring.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.biblioteca_comSpring.dominio.Livro;

public interface LivroRepository extends JpaRepository<Livro, UUID> {
    Optional<Livro> findByTitulo(String titulo);

    Livro buscarPorTitulo(String titulo);
}