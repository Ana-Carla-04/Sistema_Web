package br.edu.ufersa.SIPA.freatures.login.dto;
import br.edu.ufersa.SIPA.freatures.auth.dto.UsuarioResponseDTO;

public class LoginResponseDTO {
    private String token;
    private String tipo;
    private UsuarioResponseDTO usuario;



    public LoginResponseDTO(){}


    public LoginResponseDTO(String token, String tipo, UsuarioResponseDTO usuario){
        this.token = token;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public static LoginResponseDTO of (String token, UsuarioResponseDTO usuario){
        return new LoginResponseDTO(token, "Bearer", usuario);
    }

    public String getToken(){return token;}
    public void setToken(String token){this.token = token;}

    public String getTipo(){return tipo;}
    public void setTipo(String tipo){this.tipo = tipo;}

    public UsuarioResponseDTO getUsuario(){return usuario;}
    public void setUsuario(UsuarioResponseDTO usuario){this.usuario = usuario;}

}
