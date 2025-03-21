package payment_service.payment_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import payment_service.payment_service.config.mapper.DtoMapper;
import payment_service.payment_service.dto.promo.PromoCodeRequestDto;
import payment_service.payment_service.dto.promo.PromoCodeResponseDto;
import payment_service.payment_service.entity.PromoCode;
import payment_service.payment_service.exception.promo.PromoCodeExpiredException;
import payment_service.payment_service.exception.promo.PromoCodeNotFoundException;
import payment_service.payment_service.repository.PromoCodeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final DtoMapper mapper;

    public PromoCodeResponseDto createPromoCode(PromoCodeRequestDto promoCodeRequestDto) {
        PromoCode promoCode = mapper.convertToPromoCodeEntity(promoCodeRequestDto);
        promoCode = promoCodeRepository.save(promoCode);
        return mapper.convertToPromoCodeResponseDto(promoCode);
    }

    public PromoCodeResponseDto getPromoCodeById(Long id) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new PromoCodeNotFoundException(String.format("Promo code not found")));
        return mapper.convertToPromoCodeResponseDto(promoCode);
    }

    public PromoCode getPromoCodeByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new PromoCodeNotFoundException(String.format("Promo code not found with code: " + code)));
    }

    public BigDecimal applyDiscount(BigDecimal amount, String code) {
        PromoCode promoCode = getPromoCodeByCode(code);

        if (promoCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new PromoCodeExpiredException(String.format("Promo code has expired"));
        }

        BigDecimal discount = amount.multiply(promoCode.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
        return amount.subtract(discount);
    }
}

