package io.hexlet.spring.mapper;

import io.hexlet.spring.dto.TagCreateDTO;
import io.hexlet.spring.dto.TagDTO;
import io.hexlet.spring.dto.TagUpdateDTO;
import io.hexlet.spring.model.Tag;
import org.mapstruct.*;

@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TagMapper {
    public abstract Tag map(TagCreateDTO dto);
    public abstract TagDTO map(Tag model);
    public abstract void update(TagUpdateDTO dto, @MappingTarget Tag model);
}
