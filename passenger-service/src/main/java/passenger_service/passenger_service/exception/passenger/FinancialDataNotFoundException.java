package passenger_service.passenger_service.exception.passenger;

public class FinancialDataNotFoundException extends RuntimeException {
    public FinancialDataNotFoundException(String message) {
        super(message);
    }
}