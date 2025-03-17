package com.example.biblioteca_comSpring.controllers;

import com.example.biblioteca_comSpring.dominio.Livro;
import com.example.biblioteca_comSpring.repositories.LivroRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/livros")
public class LivroController {
    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    
    @PostMapping("/adicionar")
    public ResponseEntity<String> adicionarLivro(@RequestBody Livro livro) {
        livro.setDisponivel(true); 
        livroRepository.save(livro);
        return ResponseEntity.ok("Livro cadastrado com sucesso!");
    }

    
    @GetMapping
    public ResponseEntity<List<Livro>> listarLivros() {
        return ResponseEntity.ok(livroRepository.findAll());
    }

    @GetMapping("/buscar")
    public ResponseEntity<Livro> buscarLivroPorTitulo(@RequestParam("titulo") String titulo) {

        if (titulo == null || titulo.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Livro livro = livroRepository.buscarPorTitulo(titulo);

        // Verifica se o livro foi encontrado
        if (livro != null) {
            return ResponseEntity.ok(livro); // Retorna o livro com status 200 OK
        } else {
            return ResponseEntity.notFound().build(); // Retorna status 404 Not Found
        }
    }

    @DeleteMapping("/remover")
    public ResponseEntity<String> removerLivro(@RequestParam("titulo") String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("O título do livro não pode ser vazio!");
        }

        Optional<Livro> livro = livroRepository.findByTitulo(titulo);

        if (livro.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado!");
        }

        livroRepository.delete(livro.get());
        return ResponseEntity.ok("Livro removido com sucesso!");
    }
}