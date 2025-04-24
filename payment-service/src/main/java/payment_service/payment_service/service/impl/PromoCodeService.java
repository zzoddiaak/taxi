package payment_service.payment_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final DtoMapper mapper;

    public PromoCodeResponseDto createPromoCode(PromoCodeRequestDto promoCodeRequestDto) {
        log.info("Creating new promo code: {}", promoCodeRequestDto.getCode());
        PromoCode promoCode = mapper.convertToPromoCodeEntity(promoCodeRequestDto);
        promoCode = promoCodeRepository.save(promoCode);
        log.debug("Promo code created successfully: {}", promoCode);
        return mapper.convertToPromoCodeResponseDto(promoCode);
    }

    public PromoCodeResponseDto getPromoCodeById(Long id) {
        log.debug("Fetching promo code by ID: {}", id);
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Promo code not found for ID: {}", id);
                    return new PromoCodeNotFoundException("Promo code not found");
                });
        return mapper.convertToPromoCodeResponseDto(promoCode);
    }

    public PromoCode getPromoCodeByCode(String code) {
        log.debug("Looking up promo code by code: {}", code);
        return promoCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Promo code not found: {}", code);
                    return new PromoCodeNotFoundException("Promo code not found with code: " + code);
                });
    }

    public BigDecimal applyDiscount(BigDecimal amount, String code) {
        log.info("Applying discount for code: {} to amount: {}", code, amount);
        PromoCode promoCode = getPromoCodeByCode(code);

        if (promoCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            log.error("Attempt to use expired promo code: {}", code);
            throw new PromoCodeExpiredException("Promo code has expired");
        }

        BigDecimal discount = amount.multiply(promoCode.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
        BigDecimal finalAmount = amount.subtract(discount);
        log.debug("Discount applied. Original: {}, Discount: {}, Final: {}", amount, discount, finalAmount);
        return finalAmount;
    }
}