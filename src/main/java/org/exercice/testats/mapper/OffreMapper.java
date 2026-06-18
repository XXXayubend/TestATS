package org.exercice.testats.mapper;

import org.exercice.testats.dto.OffreDto;
import org.exercice.testats.entity.Offre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OffreMapper {
    // offre -> offre pour création
    @Mapping(target = "id", ignore = true)
    Offre toEntity(OffreDto dto);

    //offre -> offreDto pour la réponse
    OffreDto toDto(Offre offre);
}
