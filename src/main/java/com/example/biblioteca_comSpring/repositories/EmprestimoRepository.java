package com.example.biblioteca_comSpring.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.biblioteca_comSpring.dominio.Emprestimo;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, String> {
    Optional<Emprestimo> findByUsuario_CpfAndLivro_Titulo(String cpf, String tituloLivro);

}