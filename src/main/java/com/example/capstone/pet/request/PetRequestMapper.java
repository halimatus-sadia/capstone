package com.example.capstone.pet.request;

import com.example.capstone.pet.Pet;
import com.example.capstone.pet.PetMapper;
import com.example.capstone.utils.AuthUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        imports = {PetRequestStatus.class, LocalDateTime.class},
        uses = {PetMapper.class})
public abstract class PetRequestMapper {
    @Autowired
    protected AuthUtils authUtils;

    @Mapping(target = "requestedBy", expression = "java(authUtils.getLoggedInUser())")
    @Mapping(target = "status", expression = "java(PetRequestStatus.PENDING)")
    @Mapping(target = "pet", source = "pet")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    public abstract PetRequest toEntity(PetRequestReq dto, Pet pet);

    @Mapping(target = "requesterName", source = "requestedBy.name")
    @Mapping(target = "requesterContact", source = "requestedBy.email")
    public abstract PetRequestRes toDto(PetRequest petRequest);
}
