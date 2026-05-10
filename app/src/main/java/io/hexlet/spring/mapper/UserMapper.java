package io.hexlet.spring.mapper;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.dto.UserPatchDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract User map(UserCreateDTO dto);

    public abstract UserDTO map(User model);

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    @Mapping(target = "passwordDigest", source = "password", qualifiedByName = "encodePassword")
    public abstract void patch(UserPatchDTO dto, @MappingTarget User model);

    @Named("encodePassword")
    String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
