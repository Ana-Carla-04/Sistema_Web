package br.edu.ufersa.SIPA.freatures.custo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustoRepository extends JpaRepository<Custo, Long> {

    List<Custo> findByPlantioIdAndPlantioUsuarioId(Long plantioId, Long usuarioId);

    Optional<Custo> findByIdAndPlantioIdAndPlantioUsuarioId(
            Long id, Long plantioId, Long usuarioId);
}