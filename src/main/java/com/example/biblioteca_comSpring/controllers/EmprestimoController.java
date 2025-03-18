package com.example.biblioteca_comSpring.controllers;

import com.example.biblioteca_comSpring.dominio.Emprestimo;
import com.example.biblioteca_comSpring.dominio.Livro;
import com.example.biblioteca_comSpring.dominio.Usuario;
import com.example.biblioteca_comSpring.repositories.EmprestimoRepository;
import com.example.biblioteca_comSpring.repositories.LivroRepository;
import com.example.biblioteca_comSpring.repositories.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/realizar")
    public String realizarEmprestimo(@RequestParam String cpf, @RequestParam String tituloLivro) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);
        Optional<Livro> livroOpt = livroRepository.findByTitulo(tituloLivro);

        if (usuarioOpt.isEmpty()) {
            return "Usuário não encontrado!";
        }

        if (livroOpt.isEmpty() || !livroOpt.get().isDisponivel()) {
            return "Livro não encontrado ou indisponível!";
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuarioOpt.get());
        emprestimo.setLivro(livroOpt.get());
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataDevolucao(LocalDateTime.now().plusMinutes(10));

        livroOpt.get().setDisponivel(false);
        livroRepository.save(livroOpt.get());
        emprestimoRepository.save(emprestimo);

        return "Empréstimo realizado com sucesso!";
    }

    @PostMapping("/devolver")
    public String devolverLivro(@RequestParam String cpf, @RequestParam String tituloLivro) {
        Optional<Emprestimo> emprestimoOpt = 
            emprestimoRepository.findByUsuario_CpfAndLivro_Titulo(cpf, tituloLivro);

        if (emprestimoOpt.isEmpty()) {
            return "Empréstimo não encontrado!";
        }

        Emprestimo emprestimo = emprestimoOpt.get();
        LocalDateTime agora = LocalDateTime.now();
        long minutosAtraso = ChronoUnit.MINUTES.between(emprestimo.getDataDevolucao(), agora);

        if (minutosAtraso > 0) {
            double multa = 5.0 + Math.ceil(minutosAtraso / 10.0) * 8.0;
            return "Atraso de " + minutosAtraso + " minutos. Multa de R$ " + multa + ". PIX: 000.000.000-12";
        }

        Livro livro = emprestimo.getLivro();
        livro.setDisponivel(true);
        livroRepository.save(livro);
        emprestimoRepository.delete(emprestimo);

        return "Livro devolvido com sucesso!";
    }
}
