package com.example.biblioteca_comSpring.controllers;

import com.example.biblioteca_comSpring.dominio.Emprestimo;
import com.example.biblioteca_comSpring.dominio.Livro;
import com.example.biblioteca_comSpring.dominio.Usuario;
import com.example.biblioteca_comSpring.repositories.EmprestimoRepository;
import com.example.biblioteca_comSpring.repositories.LivroRepository;
import com.example.biblioteca_comSpring.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {
    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    public EmprestimoController(EmprestimoRepository emprestimoRepository, UsuarioRepository usuarioRepository, LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
    }

    // 📌 1️⃣ Realizar um empréstimo
    @PostMapping("/realizar")
    public ResponseEntity<String> realizarEmprestimo(@RequestParam UUID idUsuario, @RequestParam UUID idLivro) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        Optional<Livro> livroOpt = livroRepository.findById(idLivro);

        if (usuarioOpt.isEmpty() || livroOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário ou Livro não encontrados!");
        }

        Livro livro = livroOpt.get();

        if (!livro.isDisponivel()) {
            return ResponseEntity.badRequest().body("Livro já está emprestado!");
        }

        // Define a data e hora do empréstimo e o prazo de devolução (10 minutos depois)
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime prazoDevolucao = agora.plusMinutes(10);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuarioOpt.get());
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(agora);
        emprestimo.setDataDevolucao(prazoDevolucao);

        emprestimoRepository.save(emprestimo);

        livro.setDisponivel(false);
        livroRepository.save(livro);

        return ResponseEntity.ok("Empréstimo realizado com sucesso! Você deve devolver este livro em até 10 minutos.");
    }

    // 📌 2️⃣ Devolver um livro e calcular multa
    @PostMapping("/devolver/{idEmprestimo}")
    public ResponseEntity<String> devolverLivro(@PathVariable UUID idEmprestimo) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(idEmprestimo);

        if (emprestimoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Empréstimo não encontrado!");
        }

        Emprestimo emprestimo = emprestimoOpt.get();
        LocalDateTime agora = LocalDateTime.now();

        // Calcula o tempo de atraso
        long minutosAtraso = Duration.between(emprestimo.getDataDevolucao(), agora).toMinutes();
        double multa = 0.0;

        if (minutosAtraso > 0) {
            // Calcula a multa: R$ 5,00 por dia de atraso
            long diasAtraso = (minutosAtraso / 1440) + 1; // 1440 minutos = 1 dia
            multa = diasAtraso * 5.0;
        }

        // Atualiza a devolução no banco
        emprestimo.setDataDevolucao(agora);
        emprestimoRepository.save(emprestimo);

        Livro livro = emprestimo.getLivro();
        livro.setDisponivel(true);
        livroRepository.save(livro);

        if (multa > 0) {
            return ResponseEntity.ok("Livro devolvido com atraso. Multa devida: R$ " + multa);
        } else {
            return ResponseEntity.ok("Livro devolvido dentro do prazo. Sem multas.");
        }
    }

    // 📌 3️⃣ Consultar status de um empréstimo
    @GetMapping("/status/{idEmprestimo}")
    public ResponseEntity<String> consultarStatus(@PathVariable UUID idEmprestimo) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(idEmprestimo);

        if (emprestimoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Empréstimo não encontrado!");
        }

        Emprestimo emprestimo = emprestimoOpt.get();
        LocalDateTime agora = LocalDateTime.now();
        long minutosAtraso = Duration.between(emprestimo.getDataDevolucao(), agora).toMinutes();
        double multa = 0.0;

        if (minutosAtraso > 0) {
            long diasAtraso = (minutosAtraso / 1440) + 1;
            multa = diasAtraso * 5.0;
            return ResponseEntity.ok("Empréstimo atrasado! Multa atual: R$ " + multa);
        } else {
            return ResponseEntity.ok("Empréstimo dentro do prazo.");
        }
    }

    // 📌 4️⃣ Listar todos os empréstimos
    @GetMapping("/listar")
    public ResponseEntity<List<Emprestimo>> listarEmprestimos() {
        return ResponseEntity.ok(emprestimoRepository.findAll());
    }
}
