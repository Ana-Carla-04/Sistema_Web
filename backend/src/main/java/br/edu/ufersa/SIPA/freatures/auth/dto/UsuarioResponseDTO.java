package br.edu.ufersa.SIPA.freatures.auth.dto;

import br.edu.ufersa.SIPA.freatures.auth.Usuario;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private Boolean senhaAlterada;
    private Boolean sessoesInvalidadas;
    private Map<String, String> alertaSeguranca;

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getSenhaAlterada() { return senhaAlterada; }
    public void setSenhaAlterada(Boolean senhaAlterada) { this.senhaAlterada = senhaAlterada; }

    public Boolean getSessoesInvalidadas() { return sessoesInvalidadas; }
    public void setSessoesInvalidadas(Boolean sessoesInvalidadas) { this.sessoesInvalidadas = sessoesInvalidadas; }

    public Map<String, String> getAlertaSeguranca() { return alertaSeguranca; }
    public void setAlertaSeguranca(Map<String, String> alertaSeguranca) { this.alertaSeguranca = alertaSeguranca; }
}
