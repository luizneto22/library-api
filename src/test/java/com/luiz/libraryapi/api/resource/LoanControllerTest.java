package com.luiz.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luiz.libraryapi.api.domain.Book;
import com.luiz.libraryapi.api.domain.Loan;
import com.luiz.libraryapi.api.dto.LoanDTO;
import com.luiz.libraryapi.exception.BusinessException;
import com.luiz.libraryapi.service.BookService;
import com.luiz.libraryapi.service.LoanService;
import org.hamcrest.Matchers;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
    static final String LOAN_API = "/api/loan";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;
    
    @MockBean
    LoanService loanService;


    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new  ObjectMapper().writeValueAsString(dto);
        Book book = Book.builder().id(1l).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(Book.builder().id(1l).isbn("123").build()));

        Loan loan = Loan.builder().id(1l).customer("Fulano").book(book).loanDate(LocalDate.now()).build();

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente.")
    public void invalidIsbnCreateLoanTest() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new  ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro emprestado.")
    public void loanedBookErrorOnCreatedLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new  ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(Book.builder().id(1l).isbn("123").build()));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));

    }

}
