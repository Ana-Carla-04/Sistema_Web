package br.edu.ufersa.SIPA.freatures.plantio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//anti-IDOR É nunca usar o id e verificar se ele esta autenticado

//O Spring Data JPA GERA a implementação em tempo de execução.
public interface PlantioRepository extends JpaRepository<Plantio, Long> {

    // Anti-IDOR: garante que o plantio pertence ao usuário logado
    Optional<Plantio> findByIdAndUsuarioId(Long id, Long usuarioId);
    
    long countByUsuarioIdAndStatus(Long usuarioId, String status);
    
    // Usado pelo PlantioService.listar()
    List<Plantio> findByUsuarioId(Long usuarioId);

    //  Usados pelo DashboardService
    @Query("SELECT COUNT(p) FROM Plantio p WHERE p.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(p) FROM Plantio p WHERE p.usuario.id = :usuarioId AND p.status = 'PLANTADO'")
    Long countByUsuarioIdAndStatusPlantado(@Param("usuarioId") Long usuarioId);

    List<Plantio> findByUsuarioIdOrderByDataPlantioDesc(Long usuarioId);

}