package payment_service.payment_service.exception.promo;

public class PromoCodeExpiredException extends RuntimeException {
    public PromoCodeExpiredException(String message) {
        super(message);
    }
}

