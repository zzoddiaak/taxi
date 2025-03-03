package driver_service.driver_service.exception.car;

public class CarAlreadyAssignedException extends RuntimeException {
    public CarAlreadyAssignedException(String message) {
        super(message);
    }
}