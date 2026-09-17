package br.edu.ufersa.SIPA.freatures.plantio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlantioRepository extends JpaRepository<Plantio, Long> {

    // ---------- Anti-IDOR: garante que o plantio pertence ao usuário logado ----------
    Optional<Plantio> findByIdAndUsuarioId(Long id, Long usuarioId);

    // ---------- Usados pelo PlantioController/PlantioService ----------
    List<Plantio> findByUsuarioId(Long usuarioId);

    List<Plantio> findByUsuarioIdAndStatus(Long usuarioId, String status);

    List<Plantio> findByUsuarioIdAndDataPlantio(Long usuarioId, LocalDate dataPlantio);

    List<Plantio> findByUsuarioIdAndNomeContainingIgnoreCase(Long usuarioId, String nome);

    // ---------- Usados pelo DashboardService ----------
    @Query("SELECT COUNT(p) FROM Plantio p")
    Long countAll();

    @Query("SELECT COUNT(p) FROM Plantio p WHERE p.status = 'PLANTADO'")
    Long countByStatusPlantado();

    // ---------- Usados pelo HistoricoService ----------
    List<Plantio> findAllByOrderByDataPlantioDesc();
}