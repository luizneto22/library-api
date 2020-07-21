package com.luiz.libraryapi.api.exception.ApiErrors;

import com.luiz.libraryapi.exception.BusinessException;
import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ApiErrors {

    private List<String> errors;


    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        //bindingResult.getAllErrors().stream().forEach(erros ->  this.errors.add(erros.getDefaultMessage()));
        errors.addAll(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList()));
        //errors.stream().map(String::toString).forEach(System.out::println);
        //errors.stream().forEach(teste ->  System.out.println(teste));
    }

    public ApiErrors(BusinessException ex) {
        this.errors = new ArrayList<>();
        errors.add(ex.getMessage());
    }

    public ApiErrors(ResponseStatusException ex) {
        this.errors = new ArrayList<>();
        errors.add(ex.getReason());
    }
}
