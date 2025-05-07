package passenger_service.passenger_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import passenger_service.passenger_service.config.mapper.DtoMapper;
import passenger_service.passenger_service.dto.passenger.PassengerListResponseDto;
import passenger_service.passenger_service.dto.passenger.PassengerRequestDto;
import passenger_service.passenger_service.dto.passenger.PassengerResponseDto;
import passenger_service.passenger_service.entity.FinancialData;
import passenger_service.passenger_service.entity.Passenger;
import passenger_service.passenger_service.exception.passenger.FinancialDataNotFoundException;
import passenger_service.passenger_service.exception.passenger.InsufficientBalanceException;
import passenger_service.passenger_service.exception.passenger.PassengerNotFoundException;
import passenger_service.passenger_service.repository.FinancialDataRepository;
import passenger_service.passenger_service.repository.PassengerRepository;
import passenger_service.passenger_service.service.api.PassengerService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final FinancialDataRepository financialDataRepository;
    private final DtoMapper mapper;

    @Override
    public void updatePassengerRating(Long id, Float rating) {
        log.info("Updating rating for passenger: id={}, newRating={}", id, rating);
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Passenger not found during rating update: id={}", id);
                    return new PassengerNotFoundException("Passenger not found with id: " + id);
                });

        Double currentRating = passenger.getAverageRating();
        Integer ratingCount = passenger.getRatingCount();
        log.debug("Current rating: {}, rating count: {}", currentRating, ratingCount);

        Double newRating = ((currentRating * ratingCount) + rating) / (ratingCount + 1);
        passenger.setAverageRating(newRating);
        passenger.setRatingCount(ratingCount + 1);

        passengerRepository.save(passenger);
        log.info("New rating calculated: {}", newRating);
    }

    @Override
    public PassengerResponseDto createPassenger(PassengerRequestDto passengerRequestDto) {
        log.info("Creating passenger from DTO: {}", passengerRequestDto);
        Passenger passenger = mapper.convertToPassengerEntity(passengerRequestDto);
        passenger.setAverageRating(0.0);
        passenger.setRatingCount(0);
        passenger.setDriverRating(passengerRequestDto.getDriverRating());

        Passenger savedPassenger = passengerRepository.save(passenger);
        log.debug("Passenger saved: {}", savedPassenger);

        if (savedPassenger.getFinancialData() != null) {
            financialDataRepository.save(savedPassenger.getFinancialData());
            log.debug("Financial data saved for passenger: {}", savedPassenger.getId());
        }

        log.info("Passenger created successfully: id={}", savedPassenger.getId());
        return mapper.convertToPassengerDto(savedPassenger);
    }

    @Override
    public PassengerResponseDto updatePassenger(Long id, PassengerRequestDto passengerRequestDto) {
        log.info("Updating passenger: id={}", id);
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Passenger not found during update: id={}", id);
                    return new PassengerNotFoundException("Passenger not found with id: " + id);
                });

        log.debug("Updating passenger fields");
        existingPassenger.setFirstName(passengerRequestDto.getFirstName());
        existingPassenger.setLastName(passengerRequestDto.getLastName());
        existingPassenger.setEmail(passengerRequestDto.getEmail());
        existingPassenger.setPhoneNumber(passengerRequestDto.getPhoneNumber());
        existingPassenger.setDriverRating(passengerRequestDto.getDriverRating());

        if (existingPassenger.getFinancialData() != null) {
            log.debug("Updating existing financial data");
            FinancialData financialData = existingPassenger.getFinancialData();
            financialData.setBalance(passengerRequestDto.getBalance());
            financialData.setCardNumber(passengerRequestDto.getCardNumber());
            financialData.setCardExpiryDate(passengerRequestDto.getCardExpiryDate());
            financialData.setCardCvv(passengerRequestDto.getCardCvv());
            financialData.setPromo(passengerRequestDto.getPromo());
        } else {
            log.debug("Creating new financial data");
            FinancialData financialData = new FinancialData(null, existingPassenger, passengerRequestDto.getBalance(),
                    passengerRequestDto.getCardNumber(), passengerRequestDto.getCardExpiryDate(),
                    passengerRequestDto.getCardCvv(), passengerRequestDto.getPromo());
            existingPassenger.setFinancialData(financialData);
        }

        Passenger updatedPassenger = passengerRepository.save(existingPassenger);
        financialDataRepository.save(existingPassenger.getFinancialData());
        log.info("Passenger updated successfully: id={}", id);
        return mapper.convertToPassengerDto(updatedPassenger);
    }

    @Override
    public PassengerResponseDto getPassengerById(Long id) {
        log.debug("Fetching passenger by id: {}", id);
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Passenger not found: id={}", id);
                    return new PassengerNotFoundException("Passenger not found with id: " + id);
                });
        log.debug("Passenger found: {}", passenger);
        return mapper.convertToPassengerDto(passenger);
    }

    @Override
    public PassengerListResponseDto getAllPassengers() {
        log.info("Fetching all passengers");
        List<Passenger> passengers = passengerRepository.findAll();
        log.info("Found {} passengers", passengers.size());

        List<PassengerResponseDto> passengerResponseDtos = passengers.stream()
                .map(mapper::convertToPassengerDto)
                .toList();

        return PassengerListResponseDto.builder()
                .passengers(passengerResponseDtos)
                .build();
    }

    @Override
    public void deletePassenger(Long id) {
        log.info("Deleting passenger: id={}", id);
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Passenger not found during deletion: id={}", id);
                    return new PassengerNotFoundException("Passenger not found with id: " + id);
                });
        passengerRepository.delete(passenger);
        log.debug("Passenger deleted: id={}", id);
    }

    @Override
    public void updatePassengerBalance(Long id, Double amount) {
        log.info("Updating balance for passenger: id={}, amount={}", id, amount);
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Passenger not found during balance update: id={}", id);
                    return new PassengerNotFoundException("Passenger not found with id: " + id);
                });

        FinancialData financialData = passenger.getFinancialData();
        if (financialData == null) {
            log.error("Financial data not found for passenger: id={}", id);
            throw new FinancialDataNotFoundException("Financial data not found for passenger id: " + id);
        }

        log.debug("Current balance: {}, withdrawal amount: {}", financialData.getBalance(), amount);
        if (financialData.getBalance() < amount) {
            log.error("Insufficient balance: available={}, required={}", financialData.getBalance(), amount);
            throw new InsufficientBalanceException("Insufficient balance for passenger id: " + id);
        }

        financialData.setBalance(financialData.getBalance() - amount);
        financialDataRepository.save(financialData);
        log.info("Balance updated successfully: new balance={}", financialData.getBalance());
    }
}