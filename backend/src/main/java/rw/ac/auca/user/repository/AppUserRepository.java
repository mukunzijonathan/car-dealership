package rw.ac.auca.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.ac.auca.user.domain.AppUser;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
