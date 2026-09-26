package br.edu.ufersa.SIPA.freatures.login;

import br.edu.ufersa.SIPA.freatures.login.dto.LoginRequestDTO;
import br.edu.ufersa.SIPA.freatures.login.dto.LoginResponseDTO;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/SIPA/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(loginService.autenticar(dto));
    }

    // Com JWT (stateless), não há sessão no servidor para invalidar: o
    // "logout" é responsabilidade do cliente, que simplesmente descarta o
    // token guardado. Este endpoint fica apenas como um contrato de API
    // estável para o front-end chamar; se no futuro for necessário revogar
    // tokens antes da expiração, a solução é uma blacklist de tokens
    // (ex.: tabela ou cache com o jti/expiração), não HttpSession.
    @DeleteMapping
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}