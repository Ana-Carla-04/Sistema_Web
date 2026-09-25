package br.edu.ufersa.SIPA.freatures.login;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.auth.dto.UsuarioResponseDTO;
import br.edu.ufersa.SIPA.freatures.login.dto.LoginRequestDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/SIPA/login")
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto,
                                                    HttpServletRequest request) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(dto.getEmail());
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario = opt.get();


        if (!usuario.getSenha().equals(dto.getSenha())) {
            return ResponseEntity.status(401).build();
        }


        HttpSession session = request.getSession(true);
        session.setAttribute("idUsuario", usuario.getId());

        return ResponseEntity.ok(UsuarioResponseDTO.fromEntity(usuario));
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}