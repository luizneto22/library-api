package com.luiz.libraryapi.api.resource;

import com.luiz.libraryapi.api.domain.Book.Book;
import com.luiz.libraryapi.api.dto.BookDTO;
import com.luiz.libraryapi.api.exception.ApiErrors.ApiErrors;
import com.luiz.libraryapi.exception.BusinessException;
import com.luiz.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private ModelMapper modelMapper;

    public BookController(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@Valid @RequestBody BookDTO bookDto) {
        Book entity = modelMapper.map(bookDto, Book.class);
        entity = bookService.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);
    }

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id) {
        return bookService.getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
      Book book = bookService.getById(id)
              .orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));
      bookService.delete(book);
    }

    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id,  BookDTO bookDto) {
        return bookService.getById(id).map(book -> {
            book.setAuthor(bookDto.getAuthor());
            book.setTittle(bookDto.getAuthor());
            bookService.update(book);
            return modelMapper.map(book, BookDTO.class);
        }).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));

    }
}
