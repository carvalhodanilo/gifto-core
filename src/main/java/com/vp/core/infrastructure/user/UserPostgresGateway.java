package com.vp.core.infrastructure.user;

import com.vp.core.domain.gateway.UserGateway;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.user.User;
import com.vp.core.domain.user.UserId;
import com.vp.core.infrastructure.keycloak.KeycloakUserProvisioner;
import com.vp.core.infrastructure.user.model.UserJpaEntity;
import com.vp.core.infrastructure.user.persistence.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserPostgresGateway implements UserGateway {

    private static final Logger log = LoggerFactory.getLogger(UserPostgresGateway.class);

    private final KeycloakUserProvisioner keycloakUserProvisioner;
    private final UserJpaRepository userJpaRepository;

    public UserPostgresGateway(
            final KeycloakUserProvisioner keycloakUserProvisioner,
            final UserJpaRepository userJpaRepository
    ) {
        this.keycloakUserProvisioner = keycloakUserProvisioner;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User create(final User user) {
        String kcUserId = null;
        try {
            kcUserId = keycloakUserProvisioner.provisionInvitedUser(user);
            user.assignKeycloakUserId(kcUserId);
            final var entity = UserJpaEntity.from(user);
            final var saved = userJpaRepository.save(entity);
            return saved.toAggregate();
        } catch (final RuntimeException e) {
            if (kcUserId != null) {
                try {
                    keycloakUserProvisioner.deleteUserById(kcUserId);
                } catch (final RuntimeException cleanup) {
                    log.warn("Compensação Keycloak falhou para id {}: {}", kcUserId, cleanup.getMessage());
                }
            }
            throw e;
        }
    }

    @Override
    public void deleteById(final UserId anId) {
        userJpaRepository.deleteById(UUID.fromString(anId.getValue()));
    }

    @Override
    public Optional<User> findById(final UserId anId) {
        return userJpaRepository.findById(UUID.fromString(anId.getValue()))
                .map(UserJpaEntity::toAggregate);
    }

    @Override
    public User update(final User aT) {
        final var id = UUID.fromString(aT.getId().getValue());
        final var entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        entity.applyFrom(aT);
        return userJpaRepository.save(entity).toAggregate();
    }

    @Override
    public Pagination<User> findAll(final SearchQuery aQuery) {
        return Pagination.empty();
    }
}
