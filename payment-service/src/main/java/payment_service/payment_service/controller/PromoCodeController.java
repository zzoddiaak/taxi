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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promocodes")
@RequiredArgsConstructor
@Tag(name = "PromoCode Controller", description = "API for managing promo codes")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping
    @Operation(summary = "Create a promo code", description = "Creates a new promo code")
    public ResponseEntity<PromoCodeResponseDto> createPromoCode(@RequestBody PromoCodeRequestDto promoCodeRequestDto) {
        PromoCodeResponseDto promoCodeResponseDto = promoCodeService.createPromoCode(promoCodeRequestDto);
        return new ResponseEntity<>(promoCodeResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get promo code by ID", description = "Retrieves a promo code by its ID")
    public ResponseEntity<PromoCodeResponseDto> getPromoCodeById(@PathVariable Long id) {
        PromoCodeResponseDto promoCodeResponseDto = promoCodeService.getPromoCodeById(id);
        return new ResponseEntity<>(promoCodeResponseDto, HttpStatus.OK);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get promo code by code", description = "Retrieves a promo code by its code")
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

    @PostMapping("/apply-discount")
    @Operation(summary = "Apply discount", description = "Applies a discount using a promo code")
    public ResponseEntity<BigDecimal> applyDiscount(@RequestBody DiscountRequestDto discountRequestDto) {
        BigDecimal discountedAmount = promoCodeService.applyDiscount(discountRequestDto.getAmount(), discountRequestDto.getPromoCode());
        return new ResponseEntity<>(discountedAmount, HttpStatus.OK);
    }
}
