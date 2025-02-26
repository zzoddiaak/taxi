package driver_service.driver_service.exception.driver;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(String message) {

        super(message);
    }
}
