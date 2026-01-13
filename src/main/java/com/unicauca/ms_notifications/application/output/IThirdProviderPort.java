package com.unicauca.ms_notifications.application.output;

import java.util.Optional;

import com.unicauca.ms_notifications.domain.model.ThirdReplica;

/**
 * @brief Port interface for accessing third party data.
 * Defines the contract for third party data retrieval services.
 */
public interface IThirdProviderPort {
    Optional<ThirdReplica> findByThirdPartyId(Long thirdPartyId);
}
