package passenger_service.passenger_service.config.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import passenger_service.passenger_service.dto.financial.FinancialDataDto;
import passenger_service.passenger_service.dto.rating.RatingDto;
import passenger_service.passenger_service.entity.FinancialData;
import passenger_service.passenger_service.entity.Passenger;

@Component
public class DtoMapper {

    private final ModelMapper modelMapper;

    public DtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PassengerResponseDto convertToPassengerDto(Passenger passenger) {
        if (passenger == null) {
            return null;
        }

        RatingDto ratingDto = new RatingDto(passenger.getAverageRating(), passenger.getRatingCount());
        FinancialDataDto financialDataDto = null;

        if (passenger.getFinancialData() != null) {
            financialDataDto = new FinancialDataDto(
                    passenger.getFinancialData().getBalance(),
                    passenger.getFinancialData().getCardNumber(),
                    passenger.getFinancialData().getCardExpiryDate(),
                    passenger.getFinancialData().getCardCvv(),
                    passenger.getFinancialData().getPromo()
            );
        }

        return PassengerResponseDto.builder()
                .id(passenger.getId())
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .email(passenger.getEmail())
                .phoneNumber(passenger.getPhoneNumber())
                .rating(ratingDto)
                .financialData(financialDataDto)
                .driverRating(passenger.getDriverRating())
                .build();
    }

    public Passenger convertToPassengerEntity(PassengerRequestDto dto) {
        Passenger passenger = modelMapper.map(dto, Passenger.class);
        FinancialData financialData = new FinancialData(null, passenger, dto.getBalance(), dto.getCardNumber(), dto.getCardExpiryDate(), dto.getCardCvv(), dto.getPromo());
        passenger.setFinancialData(financialData);
        passenger.setDriverRating(dto.getDriverRating());
        return passenger;
    }
}
