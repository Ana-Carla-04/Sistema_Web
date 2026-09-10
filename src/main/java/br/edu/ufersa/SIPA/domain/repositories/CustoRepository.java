package br.edu.ufersa.SIPA.domain.repositories;

import br.edu.ufersa.SIPA.domain.entities.Custo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustoRepository extends JpaRepository<Custo, Long> {

    List<Custo> findByPlantioIdAndPlantioUsuarioId(Long plantioId, Long usuarioId);

    Optional<Custo> findByIdAndPlantioIdAndPlantioUsuarioId(
            Long id, Long plantioId, Long usuarioId);
}