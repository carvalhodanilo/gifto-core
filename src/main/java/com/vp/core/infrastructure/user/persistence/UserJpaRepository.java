package com.vp.core.infrastructure.user.persistence;

import com.vp.core.infrastructure.user.model.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
}
