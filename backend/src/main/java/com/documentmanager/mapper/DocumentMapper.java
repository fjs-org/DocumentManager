package com.documentmanager.mapper;

import com.documentmanager.dto.DocumentDto;
import com.documentmanager.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Document toEntity(DocumentDto dto);

    DocumentDto toDto(Document document);
}