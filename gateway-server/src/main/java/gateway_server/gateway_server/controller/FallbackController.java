package gateway_server.gateway_server.controller;

import gateway_server.gateway_server.exception.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/{serviceName}")
    public ResponseEntity<Map<String, Object>> serviceFallback(@PathVariable String serviceName) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ServiceUnavailableException(serviceName).getErrorDetails());
    }
}