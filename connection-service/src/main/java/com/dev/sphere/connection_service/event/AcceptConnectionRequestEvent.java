package com.dev.sphere.connection_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcceptConnectionRequestEvent {

    private Long senderId;
    private Long receiverId;
}
