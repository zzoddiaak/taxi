package gateway_server.gateway_server.exception;

import java.util.Map;

public class ServiceUnavailableException extends RuntimeException {
    private final Map<String, Object> errorDetails;

    public ServiceUnavailableException(String serviceName) {
        this.errorDetails = Map.of(
                "service", serviceName,
                "status", 503,
                "error", "Service Unavailable",
                "message", serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1) +
                        " Service is unavailable. Please try later.",
                "resolution", "Please try again later or contact support"
        );
    }

    public Map<String, Object> getErrorDetails() {
        return errorDetails;
    }
}