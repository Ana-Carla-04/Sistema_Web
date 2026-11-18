package br.edu.ufersa.SIPA.freatures.colheita;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ColheitaRepository extends JpaRepository<Colheita, Long> {

    @Query("SELECT c FROM Colheita c WHERE c.id = :id AND c.lote.usuario.id = :usuarioId")
    Optional<Colheita> findByIdAndUsuarioId(@Param("id") Long id, @Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM Colheita c WHERE c.lote.usuario.id = :usuarioId")
    List<Colheita> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM Colheita c WHERE c.lote.usuario.id = :usuarioId ORDER BY c.data DESC")
    List<Colheita> findRecentesByUsuarioId(@Param("usuarioId") Long usuarioId);

    // usado internamente pelo ColheitaService após validar o lote (evita repetir o join acima)
    List<Colheita> findByLoteId(Long plantioId);
}