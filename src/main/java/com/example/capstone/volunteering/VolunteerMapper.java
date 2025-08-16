package com.example.capstone.volunteering;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class VolunteerMapper {

    // Opportunity Mappings
    @Mapping(target = "postedById", ignore = true)
    @Mapping(target = "postedByName", ignore = true)
    public abstract VolunteerOpportunityResponse toOpportunityResponse(VolunteerOpportunity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedBy", ignore = true)
    public abstract VolunteerOpportunity toOpportunityEntity(VolunteerOpportunityRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postedBy", ignore = true)
    public abstract void updateOpportunityEntity(VolunteerOpportunityRequest request, @MappingTarget VolunteerOpportunity entity);

    // Application Mappings
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "opportunityId", ignore = true)
    @Mapping(target = "opportunityTitle", ignore = true)
    public abstract VolunteerApplicationResponse toApplicationResponse(VolunteerApplication entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "opportunity", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract VolunteerApplication toApplicationEntity(VolunteerApplicationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "opportunity", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateApplicationEntity(VolunteerApplicationRequest request, @MappingTarget VolunteerApplication entity);
} 