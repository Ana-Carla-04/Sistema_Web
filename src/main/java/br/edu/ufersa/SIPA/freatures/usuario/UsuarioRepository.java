package br.edu.ufersa.SIPA.freatures.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Usado pelo CadastroController/LoginController
    // Login: buscar o usuário pelo e-mail para comparar a senha.
    // Cadastro: checar se já existe alguém com esse e-mail antes de criar (evita duplicado).
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}