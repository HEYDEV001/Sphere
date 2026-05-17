package com.dev.sphere.notification_service.clients;
import com.dev.sphere.notification_service.dto.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ConnectionsClientFallback implements ConnectionsClient {

    @Override
    public List<PersonDto> getFirstConnections(Long userId) {
        log.warn("ConnectionsClient fallback triggered — " +
                 "connection-service unavailable for userId: {}", userId);
        return Collections.emptyList();
    }
}