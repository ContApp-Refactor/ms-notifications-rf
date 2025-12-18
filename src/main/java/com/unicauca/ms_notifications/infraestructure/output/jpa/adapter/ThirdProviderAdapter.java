package com.unicauca.ms_notifications.infraestructure.output.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.unicauca.ms_notifications.application.output.IThirdProviderPort;
import com.unicauca.ms_notifications.domain.model.ThirdReplica;
import com.unicauca.ms_notifications.infraestructure.output.jpa.repository.IThirdReplicaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ThirdProviderAdapter implements IThirdProviderPort {

    private final IThirdReplicaRepository thirdReplicaRepository;

    @Override
    public Optional<ThirdReplica> findByThirdPartyId(Long thirdPartyId) {
        log.info("Buscando tercero con ID: {}", thirdPartyId);
        return thirdReplicaRepository.findByThirdId(thirdPartyId)
                .map(entity -> new ThirdReplica(
                    entity.getId(),
                    entity.getThirdId(),
                    entity.getFullName(),
                    entity.getEmail()
                ));
    }
    
}
