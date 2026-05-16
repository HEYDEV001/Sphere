package com.dev.sphere.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultsWrapper implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<SearchResponseDto> results;
}