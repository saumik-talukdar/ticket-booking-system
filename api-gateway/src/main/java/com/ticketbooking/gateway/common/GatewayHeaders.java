package com.ticketbooking.gateway.common;

import java.util.Set;

public final class GatewayHeaders {

    private GatewayHeaders() {}

    public static final String USER_ID = "X-Internal-User-Id";

    public static final String USER_ROLE = "X-Internal-User-Role";

    public static final String REQUEST_ID = "X-Gateway-Request-Id";

    public static final Set<String> RESERVED_HEADERS =
            Set.of(
                    USER_ID,
                    USER_ROLE,
                    REQUEST_ID
            );
}