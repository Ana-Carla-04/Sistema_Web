package br.edu.ufersa.SIPA.freatures.login;

import br.edu.ufersa.SIPA.freatures.auth.TokenService;
import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import br.edu.ufersa.SIPA.freatures.auth.UsuarioRepository;
import br.edu.ufersa.SIPA.freatures.auth.dto.UsuarioResponseDTO;
import br.edu.ufersa.SIPA.freatures.login.dto.LoginRequestDTO;
import br.edu.ufersa.SIPA.freatures.login.dto.LoginResponseDTO;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class LoginService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;


    public LoginService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO dto){
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"
        ));
        if(!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }
        String token = tokenService.generateToken(usuario);
        return LoginResponseDTO.of(token, UsuarioResponseDTO.fromEntity(usuario));

    }




}
