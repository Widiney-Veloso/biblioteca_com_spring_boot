package com.example.biblioteca_comSpring.controllers;


import com.example.biblioteca_comSpring.dominio.Usuario;
import com.example.biblioteca_comSpring.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarUsuario(@RequestBody Usuario usuario) {


        if (usuarioRepository.findByCpf(usuario.getCpf()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já cadastrado com esse CPF!");
        }

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Cadastro feito com sucesso!");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String senha) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);

        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não cadastrado ou caractere incorreto!");
        }

        // Verifica se a senha está correta
        Usuario user = usuarioExistente.get();
        if (!user.getSenha().equals(senha)) {
            return ResponseEntity.badRequest().body("E-mail ou senha errada");
        }

        return ResponseEntity.ok("Login bem-sucedido! Bem-vindo, " + user.getNomeUsuario());
    }
}