package io.hexlet.spring.mapper;

import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.Tag;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class PostMapper {
    @Autowired
    private ReferenceMapper referenceMapper;

    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTags")
    public abstract Post map(PostCreateDTO dto);

    public abstract PostDTO map(Post model);

    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTags")
    public abstract void update(PostUpdateDTO dto, @MappingTarget Post model);

    @Named("mapTags")
    public List<Tag> mapTags(JsonNullable<List<Long>> tagIds) {
        if (tagIds == null || !tagIds.isPresent()) {
            return new ArrayList<>();
        }
        return referenceMapper.toEntities(tagIds.get(), Tag.class);
    }
}
