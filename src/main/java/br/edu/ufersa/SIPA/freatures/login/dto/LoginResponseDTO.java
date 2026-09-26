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


}
