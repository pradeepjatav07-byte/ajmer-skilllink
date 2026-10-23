package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.UserEntity;
import com.nexoraa.skilllink.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRole(Role role);
    List<UserEntity> findAllByRoleOrderByCreatedAtDesc(Role role);
}
