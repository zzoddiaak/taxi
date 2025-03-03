package driver_service.driver_service.exception.car;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(String message) {

        super(message);
    }
}
