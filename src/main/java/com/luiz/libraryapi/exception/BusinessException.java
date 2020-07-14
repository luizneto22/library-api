package com.luiz.libraryapi.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String mensage) {
        super(mensage);
    }
}
