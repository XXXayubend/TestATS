package org.exercice.testats.mapper;

import org.exercice.testats.dto.CandidatRequestDto;
import org.exercice.testats.dto.CandidatResponseDto;
import org.exercice.testats.entity.Candidat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidatMapper {
    //conversion candidatRequestDto -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documents", ignore = true)
    Candidat toEntity(CandidatRequestDto requestDto);

    //conversion Entity -> candidatResponseDto
    @Mapping(target = "cvFileName", ignore = true)
    @Mapping(target = "cvUrl", ignore = true)
    CandidatResponseDto toResponseDto(Candidat candidat);
}