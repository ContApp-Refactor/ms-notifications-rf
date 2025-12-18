package com.unicauca.ms_notifications.application.output;

import java.util.Optional;

import com.unicauca.ms_notifications.domain.model.ThirdReplica;

public interface IThirdProviderPort {
    Optional<ThirdReplica> findByThirdPartyId(Long thirdPartyId);
}
