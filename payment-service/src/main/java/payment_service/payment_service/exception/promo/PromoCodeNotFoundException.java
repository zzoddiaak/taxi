package payment_service.payment_service.exception.promo;

public class PromoCodeNotFoundException extends RuntimeException {
    public PromoCodeNotFoundException(String message) {
        super(message);
    }
}

