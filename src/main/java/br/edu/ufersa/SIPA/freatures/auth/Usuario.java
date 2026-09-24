package br.edu.ufersa.SIPA.freatures.auth;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tb_users")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    //construtors
    public Usuario(){}
    public Usuario(Long id){
        this.id = id;
    }
    public Usuario(String email, String senha, UserRole role){
        this.email = email; this.senha = senha; this.role = role;}
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        if (this.role == UserRole.ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );}
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Getters e Setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }


}