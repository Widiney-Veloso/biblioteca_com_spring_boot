package com.example.biblioteca_comSpring.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.biblioteca_comSpring.dominio.Livro;

public interface LivroRepository extends JpaRepository<Livro, UUID> {
    Optional<Livro> findByTitulo(String titulo);

    @Query("SELECT l FROM Livro l WHERE l.titulo = :titulo")
    Livro buscarPorTitulo(@Param("titulo") String titulo);
}