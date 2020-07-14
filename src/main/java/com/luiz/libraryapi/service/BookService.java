package com.luiz.libraryapi.service;


import com.luiz.libraryapi.api.domain.Book.Book;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);
}