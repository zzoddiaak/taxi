package payment_service.payment_service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import payment_service.payment_service.config.mapper.DtoMapper;
import payment_service.payment_service.dto.promo.PromoCodeRequestDto;
import payment_service.payment_service.dto.promo.PromoCodeResponseDto;
import payment_service.payment_service.entity.PromoCode;
import payment_service.payment_service.exception.promo.PromoCodeExpiredException;
import payment_service.payment_service.repository.PromoCodeRepository;
import payment_service.payment_service.service.impl.PromoCodeService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromoCodeServiceTest {

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private DtoMapper mapper;

    @InjectMocks
    private PromoCodeService promoCodeService;

    @Test
    void createPromoCode_ShouldCreatePromoCode_WhenValidRequest() {
        PromoCodeRequestDto promoCodeRequestDto = new PromoCodeRequestDto();
        promoCodeRequestDto.setCode("TESTCODE");
        promoCodeRequestDto.setDiscountPercentage(BigDecimal.valueOf(10));
        promoCodeRequestDto.setExpirationDate(LocalDateTime.now().plusDays(1));

        PromoCode promoCode = new PromoCode();
        promoCode.setId(1L);
        promoCode.setCode("TESTCODE");
        promoCode.setDiscountPercentage(BigDecimal.valueOf(10));
        promoCode.setExpirationDate(LocalDateTime.now().plusDays(1));

        PromoCodeResponseDto promoCodeResponseDto = new PromoCodeResponseDto();
        promoCodeResponseDto.setId(1L);
        promoCodeResponseDto.setCode("TESTCODE");

        when(mapper.convertToPromoCodeEntity(promoCodeRequestDto)).thenReturn(promoCode);
        when(promoCodeRepository.save(promoCode)).thenReturn(promoCode);
        when(mapper.convertToPromoCodeResponseDto(promoCode)).thenReturn(promoCodeResponseDto);

        PromoCodeResponseDto result = promoCodeService.createPromoCode(promoCodeRequestDto);

        assertNotNull(result);
        assertEquals("TESTCODE", result.getCode());
        verify(promoCodeRepository, times(1)).save(promoCode);
    }

    @Test
    void getPromoCodeById_ShouldReturnPromoCodeResponseDto_WhenPromoCodeExists() {
        Long promoCodeId = 1L;
        PromoCode promoCode = new PromoCode();
        promoCode.setId(promoCodeId);
        promoCode.setCode("TESTCODE");

        PromoCodeResponseDto promoCodeResponseDto = new PromoCodeResponseDto();
        promoCodeResponseDto.setId(promoCodeId);
        promoCodeResponseDto.setCode("TESTCODE");

        when(promoCodeRepository.findById(promoCodeId)).thenReturn(Optional.of(promoCode));
        when(mapper.convertToPromoCodeResponseDto(promoCode)).thenReturn(promoCodeResponseDto);

        PromoCodeResponseDto result = promoCodeService.getPromoCodeById(promoCodeId);

        assertNotNull(result);
        assertEquals(promoCodeId, result.getId());
        verify(promoCodeRepository, times(1)).findById(promoCodeId);
    }

    @Test
    void applyDiscount_ShouldApplyDiscount_WhenPromoCodeIsValid() {
        String promoCode = "TESTCODE";
        BigDecimal amount = BigDecimal.valueOf(100);
        PromoCode promoCodeEntity = new PromoCode();
        promoCodeEntity.setCode(promoCode);
        promoCodeEntity.setDiscountPercentage(BigDecimal.valueOf(10));
        promoCodeEntity.setExpirationDate(LocalDateTime.now().plusDays(1));

        when(promoCodeRepository.findByCode(promoCode)).thenReturn(Optional.of(promoCodeEntity));

        BigDecimal result = promoCodeService.applyDiscount(amount, promoCode);

        assertEquals(BigDecimal.valueOf(90), result);
        verify(promoCodeRepository, times(1)).findByCode(promoCode);
    }

    @Test
    void applyDiscount_ShouldThrowException_WhenPromoCodeIsExpired() {
        String promoCode = "TESTCODE";
        BigDecimal amount = BigDecimal.valueOf(100);
        PromoCode promoCodeEntity = new PromoCode();
        promoCodeEntity.setCode(promoCode);
        promoCodeEntity.setDiscountPercentage(BigDecimal.valueOf(10));
        promoCodeEntity.setExpirationDate(LocalDateTime.now().minusDays(1));

        when(promoCodeRepository.findByCode(promoCode)).thenReturn(Optional.of(promoCodeEntity));

        assertThrows(PromoCodeExpiredException.class, () -> promoCodeService.applyDiscount(amount, promoCode));
        verify(promoCodeRepository, times(1)).findByCode(promoCode);
    }
}