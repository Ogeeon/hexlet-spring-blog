package io.hexlet.spring.mapper;

import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.BaseEntity;
import jakarta.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class ReferenceMapper {
    @Autowired
    private EntityManager entityManager;

    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }

    public <T extends BaseEntity> List<T> toEntities(List<Long> ids, @TargetType Class<T> entityClass) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> entities = new ArrayList<>();
        for (Long id : ids) {
            T entity = entityManager.find(entityClass, id);
            if (entity == null) {
                throw new ResourceNotFoundException("Entity of type " + entityClass.getSimpleName() + " with ID " + id + " not found");
            }
            entities.add(entity);
        }

        return entities;
    }
}
