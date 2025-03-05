package payment_service.payment_service.config.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import payment_service.payment_service.dto.payment.PaymentRequestDto;
import payment_service.payment_service.dto.payment.PaymentResponseDto;
import payment_service.payment_service.dto.promo.PromoCodeRequestDto;
import payment_service.payment_service.dto.promo.PromoCodeResponseDto;
import payment_service.payment_service.entity.Payment;
import payment_service.payment_service.entity.PromoCode;

@Component
public class DtoMapper {

    private final ModelMapper modelMapper;

    public DtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <D, E> D convertToDto(E entity, Class<D> dtoClass) {
        if (entity == null) {
            return null;
        }

        return modelMapper.map(entity, dtoClass);
    }

    public <D, E> E convertToEntity(D dto, Class<E> entityClass) {
        if (dto == null) {
            return null;
        }

        return modelMapper.map(dto, entityClass);
    }


    public PaymentResponseDto convertToPaymentResponseDto(Payment entity) {
        PaymentResponseDto paymentResponseDto = modelMapper.map(entity, PaymentResponseDto.class);
        if (entity.getPromoCode() != null) {
            paymentResponseDto.setPromoCode(entity.getPromoCode().getCode());
        }
        return paymentResponseDto;
    }

    public Payment convertToPaymentEntity(PaymentRequestDto dto) {
        return modelMapper.map(dto, Payment.class);
    }

    public PromoCodeResponseDto convertToPromoCodeResponseDto(PromoCode promoCode) {
        return modelMapper.map(promoCode, PromoCodeResponseDto.class);
    }

    public PromoCode convertToPromoCodeEntity(PromoCodeRequestDto promoCodeRequestDto) {
        return modelMapper.map(promoCodeRequestDto, PromoCode.class);
    }
}

