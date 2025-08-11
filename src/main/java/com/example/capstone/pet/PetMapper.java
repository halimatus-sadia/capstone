package com.example.capstone.pet;

import com.example.capstone.auth.UserMapper;
import com.example.capstone.utils.AuthUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        imports = {BooleanUtils.class},
        uses = {UserMapper.class})
public abstract class PetMapper {
    @Autowired
    protected AuthUtils authUtils;

    public abstract PetResponseDto toDto(Pet pet);

    //    @Mapping(target = "isRequestAccepted", defaultValue = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "owner", expression = "java(authUtils.getLoggedInUser())")
    @Mapping(target = "vaccinated", expression = "java(BooleanUtils.isTrue(dto.getVaccinated()))")
    public abstract Pet toEntity(PetSaveRequest dto);

    public abstract PetSaveRequest toSaveRequest(Pet pet);

    public abstract void updatePet( PetSaveRequest dto, @MappingTarget Pet pet);
}
