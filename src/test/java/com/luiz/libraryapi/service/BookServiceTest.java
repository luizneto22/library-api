package com.luiz.libraryapi.service;

import com.luiz.libraryapi.api.domain.Book;
import com.luiz.libraryapi.exception.BusinessException;
import com.luiz.libraryapi.repository.BookRepository;
import com.luiz.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    @MockBean
    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        //cenario
        Book book = createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(bookRepository.save(book))
                .thenReturn(Book.builder().id((long) 123).isbn("123").author("fulana").tittle("AS aventuras").build());

        //execucao
        Book savedBook = bookService.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getAuthor()).isEqualTo("fulana");
        assertThat(savedBook.getTittle()).isEqualTo("AS aventuras");
    }

    @Test
    @DisplayName(" Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWhithDuplicatedISBN() {
        // cenario
        Book book = createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // execucao
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));


        //verificacoes
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já Cadastrado");
        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("fulana").tittle("AS aventuras").build();
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTittle()).isEqualTo(book.getTittle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter  um livro por id quando ele nao existe na base de dados")
    public void bookNotFoundByIdTest() {
        Long id = 1l;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> book = bookService.getById(id);

        assertThat(book.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {
        Book book = Book.builder().id(1l).build();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(book));

        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest() {
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));

        Mockito.verify(bookRepository, Mockito.never()).delete(book);


    }


    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() {
        //cenario
        long id = 1l;
        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = createValidBook();
        updatedBook.setId(id);
        Mockito.when(bookService.update(updatingBook)).thenReturn(updatedBook);

        //execucao
        Book book = bookService.update(updatingBook);

        //verificacoes
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getTittle()).isEqualTo(updatedBook.getTittle());

    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest() {
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        Mockito.verify(bookRepository, Mockito.never()).save(book);


    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        //montagem do cenario
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> books = Arrays.asList(book);
        Page<Book> page = new PageImpl<>(books, pageRequest, 1);
        Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        //execucao
        Page<Book> result = bookService.find(book, pageRequest);

        //validacoes
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(books);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}
