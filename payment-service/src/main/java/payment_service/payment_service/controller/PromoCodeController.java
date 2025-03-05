package payment_service.payment_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import payment_service.payment_service.dto.promo.PromoCodeRequestDto;
import payment_service.payment_service.dto.promo.PromoCodeResponseDto;
import payment_service.payment_service.dto.promo.DiscountRequestDto;
import payment_service.payment_service.entity.PromoCode;
import payment_service.payment_service.service.impl.PromoCodeService;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/promocodes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    // Endpoint для создания промокода
    @PostMapping
    public ResponseEntity<PromoCodeResponseDto> createPromoCode(@RequestBody PromoCodeRequestDto promoCodeRequestDto) {
        PromoCodeResponseDto promoCodeResponseDto = promoCodeService.createPromoCode(promoCodeRequestDto);
        return new ResponseEntity<>(promoCodeResponseDto, HttpStatus.CREATED);
    }

    // Endpoint для получения промокода по ID
    @GetMapping("/{id}")
    public ResponseEntity<PromoCodeResponseDto> getPromoCodeById(@PathVariable Long id) {
        PromoCodeResponseDto promoCodeResponseDto = promoCodeService.getPromoCodeById(id);
        return new ResponseEntity<>(promoCodeResponseDto, HttpStatus.OK);
    }

    // Endpoint для получения промокода по его коду
    @GetMapping("/code/{code}")
    public ResponseEntity<PromoCodeResponseDto> getPromoCodeByCode(@PathVariable String code) {
        PromoCode promoCode = promoCodeService.getPromoCodeByCode(code);
        PromoCodeResponseDto promoCodeResponseDto = new PromoCodeResponseDto(
                promoCode.getId(),
                promoCode.getCode(),
                promoCode.getDiscountPercentage(),
                promoCode.getExpirationDate()
        );
        return new ResponseEntity<>(promoCodeResponseDto, HttpStatus.OK);
    }

    // Endpoint для применения скидки с промокодом
    @PostMapping("/apply-discount")
    public ResponseEntity<BigDecimal> applyDiscount(@RequestBody DiscountRequestDto discountRequestDto) {
        BigDecimal discountedAmount = promoCodeService.applyDiscount(discountRequestDto.getAmount(), discountRequestDto.getPromoCode());
        return new ResponseEntity<>(discountedAmount, HttpStatus.OK);
    }
}
