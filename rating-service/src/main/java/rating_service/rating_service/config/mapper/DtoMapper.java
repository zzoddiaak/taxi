package rating_service.rating_service.config.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

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

}
