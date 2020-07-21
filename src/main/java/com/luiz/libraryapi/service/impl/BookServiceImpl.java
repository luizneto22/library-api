package com.luiz.libraryapi.service.impl;

import com.luiz.libraryapi.api.domain.Book;
import com.luiz.libraryapi.exception.BusinessException;
import com.luiz.libraryapi.repository.BookRepository;
import com.luiz.libraryapi.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example<Book> example = Example.of(filter, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return bookRepository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> getBookByIsbn(String s) {
        return Optional.empty();
    }
}
