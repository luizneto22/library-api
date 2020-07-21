package com.luiz.libraryapi.api.resource;

import com.luiz.libraryapi.api.domain.Book;
import com.luiz.libraryapi.api.dto.BookDTO;
import com.luiz.libraryapi.api.exception.ApiErrors.ApiErrors;
import com.luiz.libraryapi.exception.BusinessException;
import com.luiz.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


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
    public BookDTO update(@PathVariable Long id, BookDTO bookDto) {
        return bookService.getById(id).map(book -> {
            book.setAuthor(bookDto.getAuthor());
            book.setTittle(bookDto.getAuthor());
            bookService.update(book);
            return modelMapper.map(book, BookDTO.class);
        }).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));

    }

    @GetMapping()
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = bookService.find(filter, pageRequest);
        List<BookDTO> list = result.getContent().stream().map(entity -> modelMapper.map(entity, BookDTO.class)).collect(Collectors.toList());
        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
    }
}
