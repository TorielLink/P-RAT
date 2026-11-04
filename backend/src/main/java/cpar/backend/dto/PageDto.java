package cpar.backend.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageDto<T>(List<T> contenu, int page, int taille, long totalElements, int totalPages) {

    public static <T> PageDto<T> depuis(Page<?> page, List<T> contenu) {
        return new PageDto<>(contenu, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}