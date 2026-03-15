package com.vp.core.infrastructure.user;

import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.gateway.UserGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.user.User;
import com.vp.core.domain.user.UserId;
import com.vp.core.infrastructure.merchant.model.MerchantJpaEntity;
import com.vp.core.infrastructure.merchant.persistence.MerchantJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserPostgresGateway implements UserGateway {

    @Override
    public User create(User aT) {
        return null;
    }

    @Override
    public void deleteById(UserId anId) {

    }

    @Override
    public Optional<User> findById(UserId anId) {
        return Optional.empty();
    }

    @Override
    public User update(User aT) {
        return null;
    }

    @Override
    public Pagination<User> findAll(SearchQuery aQuery) {
        return null;
    }
}