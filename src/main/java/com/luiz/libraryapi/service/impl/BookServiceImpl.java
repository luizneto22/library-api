package com.luiz.libraryapi.service.impl;

import com.luiz.libraryapi.api.domain.Book.Book;
import com.luiz.libraryapi.exception.BusinessException;
import com.luiz.libraryapi.repository.BookRepository;
import com.luiz.libraryapi.service.BookService;

import java.util.Optional;

public class BookServiceImpl implements BookService {


    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book any) {
        if (bookRepository.existsByIsbn(any.getIsbn())) {
            throw new BusinessException("Isbn já Cadastrado");
        }
        return bookRepository.save(any);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getId() == null){
            throw  new IllegalArgumentException(" Book id cant be null.");
        }
        bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null){
            throw  new IllegalArgumentException(" Book id cant be null.");
        }
        return bookRepository.save(book);
    }
}
