package io.hexlet.spring.mapper;

import org.mapstruct.*;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.model.Post;

@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class PostMapper {
    @Mapping(target = "tags", ignore = true)
    public abstract Post map(PostCreateDTO dto);

    public abstract PostDTO map(Post model);

    @Mapping(target = "tags", ignore = true)
    public abstract void update(PostUpdateDTO dto, @MappingTarget Post model);
}
