package com.example.capstone.pet;

import com.example.capstone.utils.AuthUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", imports = {BooleanUtils.class})
public abstract class PetMapper {
    @Autowired
    AuthUtils authUtils;

    public abstract PetResponseDto toDto(Pet pet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "owner", expression = "java(authUtils.getLoggedInUser())")
    @Mapping(target = "vaccinated", expression = "java(BooleanUtils.isTrue(dto.getVaccinated()))")
    public abstract Pet toEntity(PetRequestDto dto);
}
