package com.luiz.libraryapi.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Loan {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String isbn;

    @Column
    private String customer;

    @OneToOne
    private Book book;

    @Column
    private LocalDate loanDate;

    @Column
    private Boolean returned;

}
