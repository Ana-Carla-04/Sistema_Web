package br.edu.ufersa.SIPA.domain.repositories;

import br.edu.ufersa.SIPA.domain.entities.Plantio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlantioRepository extends JpaRepository<Plantio, Long> {

    // ---------- Usados pelo CustoService (anti-IDOR) ----------
    Optional<Plantio> findByIdAndUsuarioId(Long id, Long usuarioId);

    // ---------- Usados pelo DashboardService ----------
    @Query("SELECT COUNT(p) FROM Plantio p")
    Long countAll();

    @Query("SELECT COUNT(p) FROM Plantio p WHERE p.status = 'PLANTADO'")
    Long countByStatusPlantado();

    List<Plantio> findAllByOrderByDataPlantioDesc();

    // ---------- Usados pelo HistoricoService ----------
    // findAllByOrderByDataPlantioDesc() já cobre esse caso

    // ---------- Usados pelo AnaliseFinanceiraService ----------
    // findAll() já vem do JpaRepository

    // ---------- Útil para o PlantioController (futuro) ----------
    List<Plantio> findByUsuarioId(Long usuarioId);
}