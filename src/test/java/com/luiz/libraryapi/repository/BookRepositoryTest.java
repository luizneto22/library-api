package com.luiz.libraryapi.repository;

import com.luiz.libraryapi.api.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists() {
        //cenario
        String isnb = "123";
        Book book = createNewBook();
        testEntityManager.persist(book);

        //execucao
        boolean exists = bookRepository.existsByIsbn(isnb);
        // verificacao
        assertThat(exists).isTrue();
    }

    private Book createNewBook() {
        return Book.builder().isbn("123").author("fulana").tittle("AS aventuras").build();
    }


    @Test
    @DisplayName("Deve retornar false quando existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExists() {
        //cenario
        String isnb = "123";
        //execucao
        boolean exists = bookRepository.existsByIsbn(isnb);
        // verificacao
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por id.")
    public void findByIdTest() {
        Book book = createNewBook();
        testEntityManager.persist(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());

        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = createNewBook();

        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getIsbn()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {
        //montagem do cenario, onde pecisa de u
        Book book = createNewBook();
        //testEntityManager.persist(book);
        bookRepository.save(book);

        Book foundBook = testEntityManager.find(Book.class, book.getId());

        bookRepository.delete(foundBook);

        Book deletedBook = testEntityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();
    }
}
