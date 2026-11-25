package br.edu.ufersa.SIPA.freatures.auth.repository;

import br.edu.ufersa.SIPA.freatures.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    List<PasswordResetToken> findByUserEmailAndUsedFalse(String userEmail);

    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true, t.usedAt = :now WHERE t.userEmail = :email AND t.used = false")
    int invalidateAllActiveTokensForEmail(@Param("email") String email, @Param("now") Instant now);
}
