package driver_service.driver_service.config.mapper;

import driver_service.driver_service.dto.driver.DriverResponseDto;
import driver_service.driver_service.dto.rating.RatingDto;
import driver_service.driver_service.entity.Driver;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DtoMapper {

    private final ModelMapper modelMapper;

    public DtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void configureMapper() {
        Converter<Driver, RatingDto> ratingConverter = context -> {
            Driver source = context.getSource();
            if (source == null) return null;
            return new RatingDto(source.getAverageRating(), source.getRatingCount(), source.getPassengerRating());
        };

        modelMapper.createTypeMap(Driver.class, RatingDto.class)
                .setConverter(ratingConverter);
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
}
