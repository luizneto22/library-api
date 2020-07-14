package com.luiz.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luiz.libraryapi.api.domain.Book.Book;
import com.luiz.libraryapi.api.dto.BookDTO;
import com.luiz.libraryapi.exception.BusinessException;
import com.luiz.libraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    public static final String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTeste() throws Exception {
        BookDTO dto = createNewBook();
        Book book = Book.builder().author(dto.getAuthor()).tittle(dto.getTittle()).isbn(dto.getIsbn()).id(dto.getId()).build();
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(book);
        String json = new ObjectMapper().writeValueAsString(dto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("tittle").value(dto.getTittle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficiente para criaçao do livro.")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro")
    public void createBookWithDuplicateIsbn() throws Exception {
        BookDTO dto = createNewBook();

        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagem = "Isbn já Cadastrado";
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException(mensagem));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagem));


    }

    @Test
    @DisplayName("Deve obter informacoes de um livro")
    public void getBookDetailsTest() throws Exception {

        //cenario (give)
        Long id = 1l;
        Book book = Book.builder()
                .author(createNewBook().getAuthor())
                .tittle(createNewBook().getTittle())
                .isbn(createNewBook().getIsbn())
                .id(id)
                .build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));


        //execucao(when)

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/").concat(String.valueOf(id)))
                .accept(MediaType.APPLICATION_JSON);

        //then
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("tittle").value(createNewBook().getTittle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    public void bookNotFoundTest() throws Exception {
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //execucao(when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/").concat(String.valueOf(1)))
                .accept(MediaType.APPLICATION_JSON);

        //then
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {

        Long id = 1l;
        Book book = Book.builder()
                .id(id)
                .build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        //execucao(when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/").concat(String.valueOf(1)))
                .accept(MediaType.APPLICATION_JSON);


        //then
        mvc
                .perform(request)
                .andExpect(status().isNoContent());

    }


    @Test
    @DisplayName("Deve retornar resource not found quando nao encontrar um livro para deletar")
    public void deleteInexistentBookTest() throws Exception {

        Long id = 1l;
        Book book = Book.builder()
                .id(id)
                .build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.empty());

        //execucao(when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/").concat(String.valueOf(1)))
                .accept(MediaType.APPLICATION_JSON);
        //then
        mvc
                .perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {

        Long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book updatingBook = Book.builder().id(1l).tittle("some tittle").author(" some author").isbn("321").build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(updatingBook));

        BDDMockito.given(bookService.update(updatingBook)).willReturn(updatingBook);
        //execucao(when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/").concat(String.valueOf(1)))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        //then
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(updatingBook.getId()))
                .andExpect(jsonPath("tittle").value(updatingBook.getTittle()))
                .andExpect(jsonPath("author").value(updatingBook.getAuthor()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest() throws Exception {
        Long id = 1l;
        Book book = Book.builder()
                .id(id)
                .build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.empty());
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        //execucao(when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/").concat(String.valueOf(1)))
                .content(json)
                .accept(MediaType.APPLICATION_JSON);
        //then
        mvc.perform(request).andExpect(status().isNotFound());
    }


    private BookDTO createNewBook() {
        return BookDTO.builder().author("Arthur").tittle("As aventuras").isbn("001").build();
    }
}
