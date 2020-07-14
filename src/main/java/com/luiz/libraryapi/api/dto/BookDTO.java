package com.luiz.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {


    private Long id;

    @NotEmpty
    private String tittle;

    @NotEmpty
    private String author;

    @NotEmpty
    private String isbn;

}
