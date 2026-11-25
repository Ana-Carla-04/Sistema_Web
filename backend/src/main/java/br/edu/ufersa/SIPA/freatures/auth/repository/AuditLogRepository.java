package br.edu.ufersa.SIPA.freatures.auth.repository;

import br.edu.ufersa.SIPA.freatures.auth.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findTop20ByTargetIdentifierOrderByCreatedAtDesc(String targetIdentifier);
}
