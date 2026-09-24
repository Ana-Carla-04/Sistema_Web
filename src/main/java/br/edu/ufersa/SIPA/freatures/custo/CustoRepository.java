package br.edu.ufersa.SIPA.freatures.custo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustoRepository extends JpaRepository<Custo, Long> {

    List<Custo> findByPlantioIdAndPlantioUsuarioId(Long plantioId, Long usuarioId);

    Optional<Custo> findByIdAndPlantioIdAndPlantioUsuarioId(
            Long id, Long plantioId, Long usuarioId);

    // ---------- Usado pelo AnaliseFinanceiraService ----------
    // (obs.: sem checagem de usuário — quem chama já validou o plantio antes)
    List<Custo> findByPlantioId(Long plantioId);

    // ---------- Usado pelo HistoricoService ----------
    List<Custo> findAllByOrderByDataDesc();

    // ---------- Usados pelo DashboardService e AnaliseFinanceiraService ----------
    @Query("SELECT COALESCE(SUM(c.valor), 0.0) FROM Custo c")
    Double sumTotalCustos();

    @Query("SELECT c.categoria, SUM(c.valor) FROM Custo c GROUP BY c.categoria")
    List<Object[]> findCustosPorCategoria();
}