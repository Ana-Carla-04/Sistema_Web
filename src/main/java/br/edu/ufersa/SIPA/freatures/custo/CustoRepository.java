package br.edu.ufersa.SIPA.freatures.custo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustoRepository extends JpaRepository<Custo, Long> {

    List<Custo> findByPlantioIdAndPlantioUsuarioId(Long plantioId, Long usuarioId);

    Optional<Custo> findByIdAndPlantioIdAndPlantioUsuarioId(Long id, Long plantioId, Long usuarioId);

    List<Custo> findByPlantioId(Long plantioId);

    //  Usado pelo HistoricoService
    @Query("SELECT c FROM Custo c WHERE c.plantio.usuario.id = :usuarioId ORDER BY c.data DESC")
    List<Custo> findByUsuarioIdOrderByDataDesc(@Param("usuarioId") Long usuarioId);

    // Usados pelo DashboardService
    @Query("SELECT COUNT(c) FROM Custo c WHERE c.plantio.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COALESCE(SUM(c.valor), 0.0) FROM Custo c WHERE c.plantio.usuario.id = :usuarioId")
    Double sumTotalCustosByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT c.categoria, SUM(c.valor) FROM Custo c WHERE c.plantio.usuario.id = :usuarioId GROUP BY c.categoria")
    List<Object[]> findCustosPorCategoriaByUsuarioId(@Param("usuarioId") Long usuarioId);
 }